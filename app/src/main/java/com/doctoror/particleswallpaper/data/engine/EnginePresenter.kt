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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.VisibleForTesting
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.Transition
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.ScenePresenter
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.base.SimpleTarget2
import io.reactivex.disposables.CompositeDisposable

class EnginePresenter(
        private val apiLevelProvider: ApiLevelProvider,
        private val configurator: SceneConfigurator,
        private val controller: EngineController,
        private val glScheduler: Scheduler,
        private val glide: RequestManager,
        private val renderer: GlSceneRenderer,
        private val schedulers: SchedulersProvider,
        private val settings: SettingsRepository,
        private val scene: ParticlesScene,
        private val scenePresenter: ScenePresenter) {

    private val disposables = CompositeDisposable()

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
    @Volatile
    var background: Bitmap? = null

    @VisibleForTesting
    @ColorInt
    @Volatile
    var backgroundColor = Color.DKGRAY

    private var backgroundDirty = false
    private var backgroundColorDirty = false

    private var lastUsedImageLoadTarget: ImageLoadTarget? = null

    var visible = false
        set(value) {
            field = value
            handleRunConstraints()
        }

    @VisibleForTesting
    @JvmField
    var run = false

    fun onCreate() {
        configurator.subscribe(scene, scenePresenter, settings, glScheduler)

        disposables.add(settings.getDotScale()
                .subscribe {
                    renderer.markParticleTextureDirty()
                })

        disposables.add(settings.getFrameDelay()
                .observeOn(glScheduler)
                .subscribe { scene.frameDelay = it })

        disposables.add(settings.getBackgroundColor()
                .doOnNext {
                    backgroundColor = it
                    backgroundColorDirty = true
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
        background = null
        glide.clear(lastUsedImageLoadTarget)
        renderer.recycle()
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
                        .asBitmap()
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
        scenePresenter.setBounds(0, 0, width, height)
        schedulers.mainThread().scheduleDirect {
            this.width = width
            this.height = height

            // Force re-apply background
            backgroundUri = null
            handleBackground(settings.getBackgroundUri().blockingFirst())
        }
    }

    fun onSurfaceCreated() {
        backgroundDirty = true
        backgroundColorDirty = true
    }

    fun onDrawFrame() {
        if (backgroundDirty) {
            backgroundDirty = false
            renderer.setBackgroundTexture(background)
        }

        if (backgroundColorDirty) {
            backgroundColorDirty = false
            renderer.setClearColor(backgroundColor)
        }

        scenePresenter.draw()
        scenePresenter.run()
    }

    private fun handleRunConstraints() {
        if (run != visible) {
            run = visible
            if (run) {
                scenePresenter.start()
            } else {
                scenePresenter.stop()
            }
        }
    }

    private fun notifyBackgroundColors() {
        if (apiLevelProvider.provideSdkInt() >= Build.VERSION_CODES.O_MR1) {
            controller.notifyColorsChanged()
        }
    }

    @TargetApi(Build.VERSION_CODES.O_MR1)
    fun onComputeColors(): WallpaperColors {
        val background = background
        return if (background != null) {
            WallpaperColors.fromBitmap(background)
        } else {
            WallpaperColors.fromDrawable(ColorDrawable(backgroundColor))
        }
    }

    private inner class ImageLoadTarget(width: Int, height: Int)
        : SimpleTarget2<Bitmap>(width, height) {

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (resource.config == Bitmap.Config.ARGB_8888
                        && resource.hasAlpha() && !resource.isPremultiplied) {
                    resource.isPremultiplied = true
                }
            }
            background = resource
            backgroundDirty = true
            notifyBackgroundColors()
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            // Should not clear background here, as we cannot now if the clear has happened before
            // or after new request is finished.
        }
    }
}
