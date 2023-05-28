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
import com.doctoror.particlesdrawable.engine.Engine
import com.doctoror.particlesdrawable.model.Scene
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.subjects.PublishSubject

class EnginePresenter(
    private val apiLevelProvider: ApiLevelProvider,
    private val backgroundLoader: EngineBackgroundLoader,
    private val configurator: SceneConfigurator,
    private val controller: EngineController,
    private val engine: Engine,
    private val renderThreadScheduler: Scheduler,
    private val renderer: EngineSceneRenderer,
    private val schedulers: SchedulersProvider,
    private val settings: SceneSettings,
    private val scene: Scene
) {

    // We need to lock on a scene because configuration change in main thread may happen during a
    // render in a GL thread and that can cause race conditions
    private val sceneLock = Any()

    private val dimensionsSubject = PublishSubject.create<WallpaperDimensions>().toSerialized()

    private val disposables = CompositeDisposable()

    @ColorInt
    @Volatile
    private var backgroundColor = Color.DKGRAY

    @Volatile
    private var background: Bitmap? = null

    @Volatile
    private var backgroundDirty = false

    @Volatile
    private var backgroundColorDirty = false

    @Volatile
    private var backgroundScroll = true

    @Volatile
    private var foregroundScroll = true

    var visible = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    engine.start()
                } else {
                    engine.stop()
                }
            }
        }

    fun onCreate() {
        configurator
            .subscribe(scene, sceneLock, engine, settings, renderThreadScheduler)

        disposables.add(settings.observeParticleScale()
            .subscribeOn(schedulers.io())
            .observeOn(renderThreadScheduler)
            .subscribe { renderer.markParticleTextureDirty() })

        disposables.add(settings.observeFrameDelay()
            .subscribeOn(schedulers.io())
            .observeOn(renderThreadScheduler)
            .subscribe { scene.frameDelay = it })

        disposables.add(settings
            .observeBackgroundColor()
            .subscribeOn(schedulers.io())
            .observeOn(renderThreadScheduler)
            .subscribe {
                backgroundColor = it
                backgroundColorDirty = true
                notifyBackgroundColors()
            }
        )

        disposables.add(
            Observable
                .combineLatest(
                    settings
                        .observeBackgroundScroll()
                        .subscribeOn(schedulers.io()),
                    settings
                        .observeParticlesScroll()
                        .subscribeOn(schedulers.io()),
                    dimensionsSubject,
                    Function3<Boolean, Boolean, WallpaperDimensions, DimensionsParameters> { scrollBg, scrollP, dimen ->
                        DimensionsParameters(scrollBg, scrollP, dimen)
                    }
                )
                .observeOn(renderThreadScheduler)
                .subscribe {
                    handleDimensions(
                        it.scrollBackground,
                        it.scrollParticles,
                        it.wallpaperDimensions
                    )
                }
        )

        backgroundLoader.onCreate()

        disposables.add(backgroundLoader
            .observeBackground()
            .observeOn(renderThreadScheduler)
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
        )
    }

    fun onDestroy() {
        backgroundLoader.onDestroy()
        visible = false
        configurator.dispose()
        disposables.clear()
        renderer.recycle()
    }

    fun setTranslationX(xPixelOffset: Float) {
        if (backgroundScroll) {
            renderer.setBackgroundTranslationX(xPixelOffset)
        }
        if (foregroundScroll) {
            renderer.setForegroundTranslationX(xPixelOffset)
        }
    }

    fun setDimensions(dimensions: WallpaperDimensions) {
        schedulers.computation().scheduleDirect {
            dimensionsSubject.onNext(dimensions)
        }
    }

    private fun handleDimensions(
        scrollBackground: Boolean,
        scrollParticles: Boolean,
        dimensions: WallpaperDimensions
    ) {
        backgroundScroll = scrollBackground
        foregroundScroll = scrollParticles

        renderer.setDimensions(dimensions.width, dimensions.height)
        if (scrollBackground) {
            renderer.overrideBackgroundDimensions(dimensions.desiredWidth, dimensions.height)
        }

        if (scrollParticles) {
            if (dimensions.width != 0 && dimensions.desiredWidth != 0) {
                configurator.setDensityMultiplier(
                    Math.min(dimensions.desiredWidth.toFloat() / dimensions.width.toFloat(), 2f)
                )
            }
        } else {
            configurator.setDensityMultiplier(1f)
        }

        engine.setDimensions(
            if (scrollParticles) dimensions.desiredWidth else dimensions.width,
            dimensions.height
        )

        backgroundLoader.setDimensions(
            if (scrollBackground) dimensions.desiredWidth else dimensions.width,
            dimensions.height
        )

        if (!scrollBackground) {
            renderer.setBackgroundTranslationX(0f)
        }

        if (!scrollParticles) {
            renderer.setForegroundTranslationX(0f)
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

        synchronized(sceneLock) {
            engine.draw()
        }
        engine.run()
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

    data class DimensionsParameters(
        val scrollBackground: Boolean,
        val scrollParticles: Boolean,
        val wallpaperDimensions: WallpaperDimensions
    )

    data class WallpaperDimensions(
        val width: Int,
        val height: Int,
        val desiredWidth: Int
    )
}
