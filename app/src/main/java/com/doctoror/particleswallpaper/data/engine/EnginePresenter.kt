/*
 * Copyright (C) 2018 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.particleswallpaper.data.engine

import android.annotation.TargetApi
import android.app.WallpaperColors
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.support.annotation.VisibleForTesting
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.disposables.CompositeDisposable

class EnginePresenter(
        private val configurator: SceneConfigurator,
        private val controller: EngineController,
        private val glide: RequestManager,
        private val schedulers: SchedulersProvider,
        private val settings: SettingsRepository,
        private val view: EngineView,
        private val apiLevelProvider: ApiLevelProvider = ApiLevelProvider()) {

    private val defaultDelay = 10L
    private val minDelay = 5L

    private val disposables = CompositeDisposable()

    private val handler = Handler(Looper.getMainLooper())

    @VisibleForTesting
    var width = 0
        private set

    @VisibleForTesting
    var height = 0
        private set

    @VisibleForTesting
    var backgroundUri: String? = null
        private set

    @VisibleForTesting
    var delay = defaultDelay
        private set

    private var lastUsedImageLoadTarget: ImageLoadTarget? = null

    var visible = false
        set(value) {
            field = value
            handleRunConstraints()
        }

    @VisibleForTesting
    var run = false
        private set(value) {
            if (field != value) {
                field = value
                if (value) {
                    view.start()
                    handler.post(drawRunnable)
                } else {
                    handler.removeCallbacks(drawRunnable)
                    view.stop()
                }
            }
        }

    fun onCreate() {
        configurator.subscribe(view.drawable, settings)

        disposables.add(settings.getFrameDelay()
                .observeOn(schedulers.mainThread())
                .subscribe { delay = it.toLong() })

        disposables.add(settings.getBackgroundColor()
                .doOnNext {
                    view.setBackgroundColor(it)
                    if (backgroundUri != null) {
                        // If background was already loaded, but color is changed afterwards.
                        notifyBackgroundColors()
                    }
                }
                .flatMap { settings.getBackgroundUri() }
                .observeOn(schedulers.mainThread())
                .subscribe { handleBackground(it) })
    }

    fun onDestroy() {
        visible = false
        configurator.dispose()
        disposables.clear()
        backgroundUri = null
        view.background = null
        glide.clear(lastUsedImageLoadTarget)
    }

    private fun handleBackground(uri: String) {
        if (backgroundUri != uri) {
            backgroundUri = uri
            glide.clear(lastUsedImageLoadTarget)
            view.background = null
            if (uri == NO_URI) {
                lastUsedImageLoadTarget = null
                notifyBackgroundColors()
            } else if (width != 0 && height != 0) {
                val target = ImageLoadTarget(width, height)
                glide
                        .load(uri)
                        .apply(RequestOptions.noAnimation())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.centerCropTransform())
                        .into(target)

                lastUsedImageLoadTarget = target
            }
        }
    }

    fun setDimensions(width: Int, height: Int) {
        this.width = width
        this.height = height
        view.setDimensions(width, height)

        // Force re-apply background
        backgroundUri = null
        handleBackground(settings.getBackgroundUri().blockingFirst())
    }

    private fun draw() {
        handler.removeCallbacks(drawRunnable)
        if (run) {
            val startTime = SystemClock.uptimeMillis()
            view.draw()
            handler.postDelayed(drawRunnable,
                    Math.max(delay - (SystemClock.uptimeMillis() - startTime), minDelay))
        }
    }

    private fun handleRunConstraints() {
        run = visible
    }

    private fun notifyBackgroundColors() {
        if (apiLevelProvider.provideSdkInt() >= Build.VERSION_CODES.O_MR1) {
            controller.notifyColorsChanged()
        }
    }

    @TargetApi(Build.VERSION_CODES.O_MR1)
    fun onComputeColors(): WallpaperColors {
        val background = view.background
        val colors: WallpaperColors
        if (background != null) {
            colors = WallpaperColors.fromDrawable(background)
            background.setBounds(0, 0, width, height)
        } else {
            colors = WallpaperColors.fromDrawable(ColorDrawable(view.backgroundPaint.color))
        }
        return colors
    }

    private val drawRunnable = Runnable { this.draw() }

    private inner class ImageLoadTarget(private val width: Int, private val height: Int)
        : SimpleTarget<Drawable>(width, height) {

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            resource.setBounds(0, 0, width, height)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (resource is BitmapDrawable) {
                    resource.bitmap?.let {
                        if (it.config == Bitmap.Config.ARGB_8888
                                && it.hasAlpha() && !it.isPremultiplied) {
                            it.isPremultiplied = true
                        }
                    }
                }
            }
            view.background = resource
            notifyBackgroundColors()
        }
    }
}
