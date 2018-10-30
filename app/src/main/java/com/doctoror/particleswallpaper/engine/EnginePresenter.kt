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
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.ScenePresenter
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class EnginePresenter(
    private val apiLevelProvider: ApiLevelProvider,
    private val backgroundLoader: EngineBackgroundLoader,
    private val configurator: SceneConfigurator,
    private val controller: EngineController,
    private val renderThreadScheduler: Scheduler,
    private val renderer: EngineSceneRenderer,
    private val schedulers: SchedulersProvider,
    private val settings: SceneSettings,
    private val scene: ParticlesScene,
    private val scenePresenter: ScenePresenter
) {

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

    var visible = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    scenePresenter.start()
                } else {
                    scenePresenter.stop()
                }
            }
        }

    fun onCreate() {
        configurator
            .subscribe(scene, scenePresenter, settings, renderThreadScheduler)

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
                    dimensionsSubject,
                    BiFunction<Boolean, WallpaperDimensions, Pair<Boolean, WallpaperDimensions>> { scroll, dimen ->
                        scroll to dimen
                    }
                )
                .observeOn(renderThreadScheduler)
                .subscribe { handleDimensions(it.first, it.second) }
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

    fun setDimensions(dimensions: WallpaperDimensions) {
        schedulers.computation().scheduleDirect {
            dimensionsSubject.onNext(dimensions)
        }
    }

    private fun handleDimensions(
        scrollBackground: Boolean,
        dimensions: WallpaperDimensions
    ) {
        renderer.setDimensions(dimensions.width, dimensions.height)
        renderer.setShouldTranslateBackground(scrollBackground)
        if (scrollBackground) {
            renderer.overrideBackgroundDimensions(dimensions.desiredWidth, dimensions.desiredHeight)
        }

        scenePresenter.setDimensions(dimensions.desiredWidth, dimensions.desiredHeight)

        if (scrollBackground) {
            backgroundLoader.setDimensions(dimensions.desiredWidth, dimensions.desiredHeight)
        } else {
            backgroundLoader.setDimensions(dimensions.width, dimensions.height)
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

    data class WallpaperDimensions(
        val width: Int,
        val height: Int,
        val desiredWidth: Int,
        val desiredHeight: Int
    )
}
