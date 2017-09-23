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
import io.reactivex.disposables.Disposable
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

        private var frameDelayDisposable: Disposable? = null
        private var backgroundDisposable: Disposable? = null
        private var backgroundColorDisposable: Disposable? = null

        private val backgroundPaint = Paint()

        private val handler = Handler(Looper.getMainLooper())
        private val drawable = ParticlesDrawable()

        private var visible = false

        private var width = 0
        private var height = 0

        private var background: Drawable? = null
        private var delay = DEFAULT_DELAY

        private var lastUsedImageLoadTarget: ImageLoadTarget? = null

        init {
            backgroundPaint.style = Paint.Style.FILL
            backgroundPaint.color = Color.BLACK
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Injector.configComponent.inject(this)

            configurator.subscribe(drawable, settings)
            glide = Glide.with(this@WallpaperServiceImpl)

            frameDelayDisposable = settings.getFrameDelay()
                    .observeOn(schedulers.mainThread())
                    .subscribe { delay = it.toLong() }

            backgroundDisposable = settings.getBackgroundUri()
                    .observeOn(schedulers.mainThread())
                    .subscribe { handleBackground(it) }

            backgroundColorDisposable = settings.getBackgroundColor()
                    .observeOn(schedulers.mainThread())
                    .subscribe { backgroundPaint.color = it }
        }

        override fun onDestroy() {
            super.onDestroy()
            visible = false
            configurator.dispose()
            frameDelayDisposable?.dispose()
            backgroundDisposable?.dispose()
            backgroundColorDisposable?.dispose()
            background = null
            glide.clear(lastUsedImageLoadTarget)
        }

        private fun handleBackground(uri: String) {
            glide.clear(lastUsedImageLoadTarget)
            background = null
            if (uri == NO_URI) {
                lastUsedImageLoadTarget = null
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

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawable.setBounds(0, 0, width, height)
            background?.setBounds(0, 0, width, height)
            this.width = width
            this.height = height
            handleBackground(settings.getBackgroundUri().blockingFirst())
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(drawRunnable)
            drawable.stop()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.visible = visible
            if (visible) {
                drawable.start()
                handler.post(drawRunnable)
            } else {
                handler.removeCallbacks(drawRunnable)
                drawable.stop()
            }
        }

        private fun draw() {
            handler.removeCallbacks(drawRunnable)
            if (visible) {
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

        private fun drawBackgroundColor(c: Canvas) {
            c.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        }

        private val drawRunnable = Runnable { this.draw() }

        private inner class ImageLoadTarget(width: Int, height: Int)
            : SimpleTarget<Drawable>(width, height) {

            override fun onResourceReady(resource: Drawable?, transition: Transition<in Drawable>?) {
                resource?.setBounds(0, 0, width, height)
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
            }
        }
    }
}
