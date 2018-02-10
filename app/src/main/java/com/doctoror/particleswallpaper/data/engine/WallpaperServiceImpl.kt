/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.di.Injector
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by Yaroslav Mytkalyk on 18.04.17.
 *
 * The [WallpaperService] implementation.
 */
class WallpaperServiceImpl : WallpaperService() {

    override fun onCreateEngine(): Engine = EngineImpl()

    internal companion object EngineConfig {

        private const val TAG = "WallpaperService:Engine"

        private const val DEFAULT_DELAY = 10L
        private const val MIN_DELAY = 5L
    }

    inner class EngineImpl : Engine() {

        @Inject lateinit var schedulers: SchedulersProvider
        @Inject lateinit var configurator: SceneConfigurator
        @Inject lateinit var settings: SettingsRepository

        private lateinit var glide: RequestManager

        private val disposables = CompositeDisposable()

        private val backgroundPaint = Paint()

        private val handler = Handler(Looper.getMainLooper())
        private val drawable = ParticlesDrawable()

        private var width = 0
        private var height = 0

        private var backgroundUri: String? = null
        private var background: Drawable? = null
        private var delay = DEFAULT_DELAY

        private var lastUsedImageLoadTarget: ImageLoadTarget? = null

        private var visible = false
            set(value) {
                field = value
                handleRunConstraints()
            }

        private var run = false
            set(value) {
                if (field != value) {
                    field = value
                    if (value) {
                        drawable.start()
                        handler.post(drawRunnable)
                    } else {
                        handler.removeCallbacks(drawRunnable)
                        drawable.stop()
                    }
                }
            }

        init {
            backgroundPaint.style = Paint.Style.FILL
            backgroundPaint.color = Color.BLACK
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Injector.configComponent.inject(this)

            configurator.subscribe(drawable, settings)
            glide = Glide.with(this@WallpaperServiceImpl)

            disposables.add(settings.getFrameDelay()
                    .observeOn(schedulers.mainThread())
                    .subscribe { delay = it.toLong() })

            disposables.add(settings.getBackgroundColor()
                    .doOnNext {
                        backgroundPaint.color = it
                        if (backgroundUri != null) {
                            // If background was already loaded, but color is changed afterwards.
                            notifyBackgroundColors()
                        }
                    }
                    .flatMap { settings.getBackgroundUri() }
                    .observeOn(schedulers.mainThread())
                    .subscribe { handleBackground(it) })
        }

        override fun onDestroy() {
            super.onDestroy()
            visible = false
            configurator.dispose()
            disposables.clear()
            backgroundUri = null
            background = null
            glide.clear(lastUsedImageLoadTarget)
        }

        private fun handleBackground(uri: String) {
            if (backgroundUri != uri) {
                backgroundUri = uri
                glide.clear(lastUsedImageLoadTarget)
                background = null
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

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawable.setBounds(0, 0, width, height)
            background?.setBounds(0, 0, width, height)
            this.width = width
            this.height = height

            // Force re-apply background
            backgroundUri = null
            handleBackground(settings.getBackgroundUri().blockingFirst())
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.visible = visible
        }

        private fun draw() {
            handler.removeCallbacks(drawRunnable)
            if (run) {
                val startTime = SystemClock.uptimeMillis()
                val holder = surfaceHolder
                var canvas: Canvas? = null
                try {
                    canvas = holder.lockCanvas()
                    if (canvas != null) {
                        drawBackground(canvas)
                        drawable.draw(canvas)
                        drawable.nextFrame()
                    }
                } finally {
                    canvas?.let {
                        try {
                            holder.unlockCanvasAndPost(it)
                        } catch (e: IllegalArgumentException) {
                            Log.wtf(TAG, e)
                        }
                    }
                }
                handler.postDelayed(drawRunnable,
                        Math.max(delay - (SystemClock.uptimeMillis() - startTime), MIN_DELAY))
            }
        }

        private fun drawBackground(c: Canvas) {
            val background = background
            if (background == null) {
                drawBackgroundColor(c)
            } else {
                if (background is BitmapDrawable) {
                    background.bitmap?.let {
                        if (it.hasAlpha()) {
                            drawBackgroundColor(c)
                        }
                    }
                }
                background.draw(c)
            }
        }

        private fun handleRunConstraints() {
            run = visible
        }

        private fun drawBackgroundColor(c: Canvas) {
            c.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        }

        private fun notifyBackgroundColors() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                notifyColorsChanged()
            }
        }

        @TargetApi(Build.VERSION_CODES.O_MR1)
        override fun onComputeColors(): WallpaperColors {
            val background = background
            return if (background != null) {
                WallpaperColors.fromDrawable(background)
            } else {
                WallpaperColors(
                        Color.valueOf(backgroundPaint.color),
                        Color.valueOf(drawable.dotColor),
                        Color.valueOf(drawable.lineColor))
            }
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
                background = resource
                notifyBackgroundColors()
            }
        }
    }
}
