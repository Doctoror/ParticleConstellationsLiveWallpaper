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
package com.doctoror.particleswallpaper.engine

import android.annotation.TargetApi
import android.app.WallpaperColors
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.ScenePresenter
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.glide.CenterCropAndThenResizeTransform
import com.doctoror.particleswallpaper.framework.util.Optional
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

class EnginePresenter(
    private val apiLevelProvider: ApiLevelProvider,
    private val configurator: SceneConfigurator,
    private val controller: EngineController,
    private val glScheduler: Scheduler,
    private val glide: RequestManager,
    private val renderer: EngineSceneRenderer,
    private val schedulers: SchedulersProvider,
    private val settings: SceneSettings,
    private val settingsOpenGL: OpenGlSettings,
    private val scene: ParticlesScene,
    private val scenePresenter: ScenePresenter,
    private val textureDimensionsCalculator: TextureDimensionsCalculator
) {

    private val disposables = CompositeDisposable()

    private var bgDisposable: Disposable? = null

    @VisibleForTesting
    var width = 0
        private set

    @VisibleForTesting
    var height = 0
        private set

    @VisibleForTesting
    @Volatile
    var background: Bitmap? = null

    @VisibleForTesting
    @ColorInt
    @Volatile
    var backgroundColor = Color.DKGRAY

    @Volatile
    private var backgroundDirty = false

    @Volatile
    private var backgroundColorDirty = false

    private var lastUsedImageLoadSettings: ImageLoadSettings? = null

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

        disposables.add(settings.observeParticleScale()
            .subscribe {
                renderer.markParticleTextureDirty()
            })

        disposables.add(settings.observeFrameDelay()
            .observeOn(glScheduler)
            .subscribe { scene.frameDelay = it })

        disposables.add(settings
            .observeBackgroundColor()
            .subscribe {
                backgroundColor = it
                backgroundColorDirty = true
                notifyBackgroundColors()
            }
        )
    }

    fun onDestroy() {
        visible = false
        configurator.dispose()
        disposables.clear()
        bgDisposable?.dispose()
        bgDisposable = null
        background = null
        lastUsedImageLoadSettings = null
        renderer.recycle()
    }

    private fun subscribeToBackground(width: Int, height: Int) {
        if (width != this.width || height != this.height) {
            this.width = width
            this.height = height
            bgDisposable?.dispose()
            lastUsedImageLoadSettings = null
            bgDisposable = imageLoadSettingsSource()
                .filter { lastUsedImageLoadSettings != it }
                .map { settings ->
                    lastUsedImageLoadSettings = settings
                    Optional(
                        if (settings.uri == NO_URI) {
                            null
                        } else {
                            loadResource(width, height, settings)
                        }
                    )
                }
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribe(
                    {
                        background = it.value
                        backgroundDirty = true
                        notifyBackgroundColors()
                    },
                    {
                        Log.w("EnginePresenter", "Failed to load resource", it)
                    }
                )
        }
    }

    private fun imageLoadSettingsSource() = Observable
        .combineLatest(
            settingsOpenGL.observeOptimizeTextures(),
            settings.observeBackgroundUri(),
            BiFunction<Boolean, String, ImageLoadSettings> { optimize, uri ->
                ImageLoadSettings(
                    optimize,
                    uri
                )
            }
        )

    @WorkerThread
    private fun loadResource(width: Int, height: Int, settings: ImageLoadSettings): Bitmap {
        val targetDimensions = textureDimensionsCalculator
            .calculateTextureDimensions(width, height, settings.optimize)

        return glide
            .asBitmap()
            .load(settings.uri)
            .apply(RequestOptions.noAnimation())
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .apply(makeTransformOptions(targetDimensions))
            .submit(targetDimensions.first, targetDimensions.second)
            .get()
    }

    private fun makeTransformOptions(targetDimensions: android.util.Pair<Int, Int>) =
        if (targetDimensions.first != width || targetDimensions.second != height) {
            RequestOptions.bitmapTransform(
                CenterCropAndThenResizeTransform(
                    targetDimensions.first,
                    targetDimensions.second
                )
            )
        } else {
            RequestOptions.centerCropTransform()
        }

    fun setDimensions(width: Int, height: Int) {
        scenePresenter.setBounds(0, 0, width, height)
        subscribeToBackground(width, height)
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

    private data class ImageLoadSettings(
        val optimize: Boolean,
        val uri: String
    )
}
