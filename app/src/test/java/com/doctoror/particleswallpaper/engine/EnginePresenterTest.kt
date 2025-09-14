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

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import com.doctoror.particlesdrawable.engine.Engine
import com.doctoror.particlesdrawable.model.Scene
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.util.Optional
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class EnginePresenterTest {

    private val apiLevelProvider: ApiLevelProvider = mock()
    private val backgroundImageDimensionsTransformer: BackgroundImageDimensionsTransformer = mock()
    private val backgroundLoader: EngineBackgroundLoader = mock()
    private val configurator: SceneConfigurator = mock()
    private val controller: EngineController = mock()
    private val engine: Engine = mock()
    private val renderer: EngineSceneRenderer = mock()
    private val settings: SceneSettings = mock()
    private val scene: Scene = mock()

    private val underTest = EnginePresenter(
        apiLevelProvider,
        backgroundImageDimensionsTransformer,
        backgroundLoader,
        configurator,
        controller,
        engine,
        Schedulers.trampoline(),
        renderer,
        TrampolineSchedulers(),
        settings,
        scene
    )

    @Before
    fun setup() {
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION.SDK_INT)

        whenever(backgroundImageDimensionsTransformer.transform(any(), any()))
            .thenAnswer { it.arguments[1] }

        whenever(backgroundLoader.observeBackground())
            .thenReturn(Observable.just(Optional(null)))

        whenever(settings.observeBackgroundColor()).thenReturn(Observable.just(0))
        whenever(settings.observeBackgroundScroll()).thenReturn(Observable.just(true))
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(NO_URI))
        whenever(settings.backgroundUri).thenReturn(NO_URI)
        whenever(settings.observeFrameDelay()).thenReturn(Observable.just(0))
        whenever(settings.observeParticleScale()).thenReturn(Observable.just(1f))
        whenever(settings.observeParticlesScroll()).thenReturn(Observable.just(true))
    }

    @After
    fun tearDown() {
        underTest.onDestroy()
        stopKoin()
    }

    @Test
    fun subscribesToConfigurator() {
        // When
        underTest.onCreate()

        // Then
        verify(configurator).subscribe(
            eq(scene),
            any(),
            eq(engine),
            eq(settings),
            eq(Schedulers.trampoline())
        )
    }

    @Test
    fun setsBackgroundColorOnDrawFrameWhenSurfaceCreated() {
        // Given
        val color = 666
        whenever(settings.observeBackgroundColor()).thenReturn(Observable.just(color))

        // When
        underTest.onCreate()
        underTest.onSurfaceCreated()
        underTest.onDrawFrame()

        // Then
        verify(renderer).setClearColor(color)
    }

    @Test
    fun doesNotNotifyColorsChangedWhenSdkIsLessThanOMr1() {
        // Given
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.O)

        // When
        underTest.onCreate()

        // Then
        verify(controller, never()).notifyColorsChanged()
    }

    @Test
    fun notifiesColorsChangedWhenBackgroundColorIsLoaded() {
        // Given
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.O_MR1)
        whenever(backgroundLoader.observeBackground()).thenReturn(Observable.never())

        // When
        underTest.onCreate()

        // Then
        verify(controller).notifyColorsChanged()
    }

    @Test
    fun notifiesColorsChangedEveryTimeBackgroundColorIsLoaded() {
        // Given
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.O_MR1)
        whenever(backgroundLoader.observeBackground()).thenReturn(Observable.never())
        whenever(settings.observeBackgroundColor())
            .thenReturn(Observable.just(Color.BLACK, Color.CYAN))

        // When
        underTest.onCreate()

        // Then
        verify(controller, times(2)).notifyColorsChanged()
    }

    @Test
    fun notifiesColorsChangedWhenBackgroundImageIsLoaded() {
        // Given
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.O_MR1)
        whenever(settings.observeBackgroundColor()).thenReturn(Observable.never())
        whenever(backgroundLoader.observeBackground())
            .thenReturn(Observable.just(Optional<Bitmap>(null)))

        // When
        underTest.onCreate()

        // Then
        verify(controller).notifyColorsChanged()
    }

    @Test
    fun notifiesColorsChangedEveryTimeBackgroundImageIsLoaded() {
        // Given
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.O_MR1)
        whenever(settings.observeBackgroundColor()).thenReturn(Observable.never())
        whenever(backgroundLoader.observeBackground())
            .thenReturn(Observable.just(Optional<Bitmap>(null), Optional(mock())))

        // When
        underTest.onCreate()

        // Then
        verify(controller, times(2)).notifyColorsChanged()
    }

    @Test
    fun loadsFrameDelay() {
        // Given
        val frameDelay = 666
        whenever(settings.observeFrameDelay()).thenReturn(Observable.just(frameDelay))

        // When
        underTest.onCreate()

        // Then
        verify(scene).frameDelay = frameDelay
    }

    @Test
    fun notVisibleAfterDestroy() {
        // Given
        underTest.visible = true

        // When
        underTest.onDestroy()

        // Then
        assertFalse(underTest.visible)
    }

    @Test
    fun destroysBackgroundImageLoaderOnDestroy() {
        // When
        underTest.onDestroy()

        // Then
        verify(backgroundLoader).onDestroy()
    }

    @Test
    fun forwardsSurfaceDimensionsToEngineWhenParticlesScrollDisabled() {
        // Given
        val width = 1
        val height = 2
        val desiredWidth = 3

        whenever(settings.observeParticlesScroll()).thenReturn(Observable.just(false))

        // When
        underTest.onCreate()
        underTest.setDimensions(
            EnginePresenter.WallpaperDimensions(
                width,
                height,
                desiredWidth
            )
        )

        // Then
        verify(engine).setDimensions(width, height)
    }

    @Test
    fun forwardsDesiredDimensionsToEngineWhenParticlesScrollEnabled() {
        // Given
        val width = 320
        val height = 240
        val desiredWidth = 480

        whenever(settings.observeParticlesScroll()).thenReturn(Observable.just(true))

        // When
        underTest.onCreate()
        underTest.setDimensions(
            EnginePresenter.WallpaperDimensions(
                width,
                height,
                desiredWidth
            )
        )

        // Then
        verify(engine).setDimensions(desiredWidth, height)
    }

    @Test
    fun forwardsSurfaceDimensionsToBackgroundLoaderWhenBackgroundScrollDisabled() {
        // Given
        val width = 320
        val height = 240
        val desiredWidth = 480

        whenever(settings.observeBackgroundScroll()).thenReturn(Observable.just(false))

        // When
        underTest.onCreate()
        underTest.setDimensions(
            EnginePresenter.WallpaperDimensions(
                width,
                height,
                desiredWidth
            )
        )

        // Then
        verify(backgroundLoader).setDimensions(width, height)
        verify(renderer).setBackgroundTranslationX(0f)
    }

    @Test
    fun forwardsDesiredDimensionsToBackgroundLoaderWhenBackgroundScrollEnabled() {
        // Given
        val dimensions = EnginePresenter.WallpaperDimensions(
            240,
            320,
            480
        )

        val dimensionsTransformed = EnginePresenter.WallpaperDimensions(
            240,
            320,
            260
        )

        whenever(backgroundImageDimensionsTransformer.transform(NO_URI, dimensions))
            .thenReturn(dimensionsTransformed)

        whenever(settings.observeBackgroundScroll()).thenReturn(Observable.just(true))

        // When
        underTest.onCreate()
        underTest.setDimensions(dimensions)

        // Then
        verify(backgroundLoader).setDimensions(
            dimensionsTransformed.desiredWidth,
            dimensionsTransformed.height
        )
    }

    @Test
    fun forwardsDensityMutiplierToConfiguratorIfParticlesScrollEnabled() {
        // Given
        val width = 320
        val height = 320
        val desiredWidth = 480

        whenever(settings.observeParticlesScroll()).thenReturn(Observable.just(true))

        // When
        underTest.onCreate()
        underTest.setDimensions(
            EnginePresenter.WallpaperDimensions(
                width,
                height,
                desiredWidth
            )
        )

        // Then
        verify(configurator).setDensityMultiplier(1.5f)
    }

    @Test
    fun forwardDensityMutiplierOneIfParticlesScrollDisabled() {
        // Given
        val width = 320
        val height = 320
        val desiredWidth = 480

        whenever(settings.observeParticlesScroll()).thenReturn(Observable.just(false))

        // When
        underTest.onCreate()
        underTest.setDimensions(
            EnginePresenter.WallpaperDimensions(
                width,
                height,
                desiredWidth
            )
        )

        // Then
        verify(configurator).setDensityMultiplier(1f)
    }

    @Test
    fun overridesBackgroundDimensionsIfBackgroundScrollEnabled() {
        // Given
        val width = 320
        val height = 240
        val desiredWidth = 480

        whenever(settings.observeBackgroundScroll()).thenReturn(Observable.just(true))

        // When
        underTest.onCreate()
        underTest.setDimensions(
            EnginePresenter.WallpaperDimensions(
                width,
                height,
                desiredWidth
            )
        )

        // Then
        verify(renderer).overrideBackgroundDimensions(desiredWidth, height)
    }

    @Test
    fun doesNotOverrideBackgroundDimensionsIfBackgroundScrollDisabled() {
        // Given
        val width = 320
        val height = 240
        val desiredWidth = 480

        whenever(settings.observeBackgroundScroll()).thenReturn(Observable.just(false))

        // When
        underTest.onCreate()
        underTest.setDimensions(
            EnginePresenter.WallpaperDimensions(
                width,
                height,
                desiredWidth
            )
        )

        // Then
        verify(renderer, never()).overrideBackgroundDimensions(any(), any())
    }

    @Test
    fun startsWhenVisible() {
        // When
        underTest.visible = true

        // Then
        verify(engine).start()
    }

    @Test
    fun stopsWhenNotVisible() {
        // Given
        underTest.visible = true

        // When
        underTest.visible = false

        // Then
        verify(engine).stop()
    }

    @Test
    fun onDrawFrameSetsBackgroundAndColorOnceWhenDirty() {
        val background: Bitmap = mock()
        whenever(backgroundLoader.observeBackground())
            .thenReturn(Observable.just(Optional(background)))

        val color = Color.CYAN
        whenever(settings.observeBackgroundColor())
            .thenReturn(Observable.just(color))

        underTest.onCreate()
        underTest.setDimensions(EnginePresenter.WallpaperDimensions(1, 1, 2))
        underTest.onSurfaceCreated()

        underTest.onDrawFrame()
        underTest.onDrawFrame()

        verify(renderer).setBackgroundTexture(background)
        verify(renderer).setClearColor(color)

        verify(engine, times(2)).draw()
        verify(engine, times(2)).run()
    }
}
