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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
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

    override fun onCreateEngine(): Engine {
        return EngineImpl()
    }

    inner class EngineImpl : Engine() {

        private val TAG = "WallpaperService:Engine"

        @Inject lateinit var schedulers: SchedulersProvider
        @Inject lateinit var configurator: SceneConfigurator
        @Inject lateinit var settings: SettingsRepository

        private lateinit var glide: RequestManager

        private var frameDelayDisposable: Disposable? = null
        private var backgroundDisposable: Disposable? = null
        private var backgroundColorDisposable: Disposable? = null

        private val DEFAULT_DELAY = 10L
        private val MIN_DELAY = 5L

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
                    .subscribe({ d -> delay = d.toLong() })

            backgroundDisposable = settings.getBackgroundUri()
                    .observeOn(schedulers.mainThread())
                    .subscribe({ u -> handleBackground(u) })

            backgroundColorDisposable = settings.getBackgroundColor()
                    .observeOn(schedulers.mainThread())
                    .subscribe({ c -> backgroundPaint.color = c })
        }

        override fun onDestroy() {
            super.onDestroy()
            visible = false
            configurator.dispose()
            frameDelayDisposable?.dispose()
            backgroundDisposable?.dispose()
            backgroundColorDisposable?.dispose()
        }

        private fun handleBackground(uri: String) {
            glide.clear(lastUsedImageLoadTarget)
            if (uri == NO_URI) {
                background = null
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

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                      height: Int) {
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
            handler.removeCallbacks(mDrawRunnable)
            drawable.stop()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.visible = visible
            if (visible) {
                drawable.start()
                handler.post(mDrawRunnable)
            } else {
                handler.removeCallbacks(mDrawRunnable)
                drawable.stop()
            }
        }

        private fun draw() {
            handler.removeCallbacks(mDrawRunnable)
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
                    if (canvas != null) {
                        try {
                            holder.unlockCanvasAndPost(canvas)
                        } catch (e: IllegalArgumentException) {
                            Log.wtf(TAG, e)
                        }
                    }
                }
                handler.postDelayed(mDrawRunnable,
                        Math.max(delay - (SystemClock.uptimeMillis() - startTime), MIN_DELAY))
            }
        }

        private fun drawBackground(c: Canvas) {
            val background = background
            if (background == null) {
                c.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
            } else {
                background.draw(c)
            }
        }

        private val mDrawRunnable = Runnable { this.draw() }

        private inner class ImageLoadTarget(width: Int, height: Int)
            : SimpleTarget<Drawable>(width, height) {

            override fun onResourceReady(resource: Drawable?, transition: Transition<in Drawable>?) {
                resource?.setBounds(0, 0, width, height)
                background = resource
            }
        }
    }
}
