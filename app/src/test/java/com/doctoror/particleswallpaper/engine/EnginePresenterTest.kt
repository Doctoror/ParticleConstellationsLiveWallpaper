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

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.ScenePresenter
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EnginePresenterTest {

    private val requestBuilder: RequestBuilder<Bitmap> = mock()

    private val apiLevelProvider: ApiLevelProvider = mock()
    private val configurator: SceneConfigurator = mock()
    private val controller: EngineController = mock()
    private val glide: RequestManager = mock()
    private val renderer: EngineSceneRenderer = mock()
    private val settings: SceneSettings = mock()
    private val settingsOpenGL: OpenGlSettings = mock()
    private val scene: ParticlesScene = mock()
    private val scenePresenter: ScenePresenter = mock()

    private val underTest = EnginePresenter(
        apiLevelProvider,
        configurator,
        controller,
        Schedulers.trampoline(),
        glide,
        renderer,
        TrampolineSchedulers(),
        settings,
        settingsOpenGL,
        scene,
        scenePresenter,
        TextureDimensionsCalculator()
    )

    @Before
    fun setup() {
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION.SDK_INT)
        whenever(settings.observeBackgroundColor()).thenReturn(Observable.just(0))
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(NO_URI))
        whenever(settings.backgroundUri).thenReturn(NO_URI)
        whenever(settings.observeFrameDelay()).thenReturn(Observable.just(0))
        whenever(settings.observeParticleScale()).thenReturn(Observable.just(1f))
        whenever(settingsOpenGL.observeOptimizeTextures()).thenReturn(Observable.just(true))

        whenever(glide.asBitmap()).thenReturn(requestBuilder)
        whenever(requestBuilder.load(any<String>())).thenReturn(requestBuilder)
        whenever(requestBuilder.apply(any())).thenReturn(requestBuilder)
    }

    @After
    fun tearDown() {
        underTest.onDestroy()
        StandAloneContext.stopKoin()
    }

    @Test
    fun subscribesToConfigurator() {
        // When
        underTest.onCreate()

        // Then
        verify(configurator).subscribe(scene, scenePresenter, settings, Schedulers.trampoline())
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
    fun loadsTextureOptimizationSettingBeforeBackgroundUri() {
        // Given
        whenever(settingsOpenGL.observeOptimizeTextures()).thenReturn(Observable.just(false))
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.never())

        // When
        underTest.onCreate()

        // Then
        assertEquals(false, underTest.optimizeTextures)
        assertEquals(null, underTest.backgroundUri)
    }

    @Test
    fun loadsBackgroundUri() {
        // Given
        val uri = "uri://scheme"
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(uri))

        // When
        underTest.onCreate()

        // Then
        assertEquals(uri, underTest.backgroundUri)
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
    fun notifiesColorsChangedWhenUriIsBlankFor0Mr1() {
        // Given
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.O_MR1)

        // When
        underTest.onCreate()

        // Then
        verify(controller).notifyColorsChanged()
    }

    @Test
    fun clearsLastUsedImageTargetWhenUriIsLoaded() {
        // When
        underTest.onCreate()

        // Then
        verify(glide).clear(null)
    }

    @Test
    fun doesNotLoadUriWhenWidthOrHeightIs0() {
        // Given
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just("content://"))

        // When
        underTest.onCreate()

        // Then
        verify(glide, never()).load(any<Uri>())
    }

    @Test
    fun loadsUriWhenWidthOrHeightIsNot0AfterOnCreate() {
        // Given
        val uri = "content://"
        whenever(settings.backgroundUri).thenReturn(uri)

        // When
        underTest.onCreate()
        underTest.setDimensions(1, 1)

        // Then
        verify(requestBuilder).load(uri)
    }

    @Test
    fun loadsUriWhenWidthOrHeightIsNot0BeforeOnCreate() {
        // Given
        val uri = "content://"
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(uri))

        // When
        underTest.setDimensions(1, 1)
        underTest.onCreate()

        // Then
        verify(requestBuilder).load(uri)
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
    fun runWhenVisible() {
        // When
        underTest.visible = true

        // Then
        assertTrue(underTest.run)
    }

    @Test
    fun doNotRunWhenVisibilityChangedToFalse() {
        // Given
        underTest.visible = true

        // When
        underTest.visible = false

        // Then
        assertFalse(underTest.run)
    }

    @Test
    fun doNotRunOnDestroy() {
        // Given
        underTest.visible = true

        // When
        underTest.onDestroy()

        // Then
        assertFalse(underTest.run)
    }

    @Test
    fun widthAndHeightChangedOnSurfaceChange() {
        // Given
        val width = 1
        val height = 2

        // When
        underTest.setDimensions(width, height)

        // Then
        assertEquals(width, underTest.width)
        assertEquals(height, underTest.height)
    }

    @Test
    fun setsScenePresenterDimensions() {
        // Given
        val width = 1
        val height = 2

        // When
        underTest.setDimensions(width, height)

        // Then
        verify(scenePresenter).setBounds(0, 0, width, height)
    }

    @Test
    fun startsWhenVisible() {
        // When
        underTest.visible = true

        // Then
        verify(scenePresenter).start()
    }

    @Test
    fun stopsWhenNotVisible() {
        // Given
        underTest.visible = true

        // When
        underTest.visible = false

        // Then
        verify(scenePresenter).stop()
    }

    @Test
    fun onDrawFrameWhenBackgroundAndColorDirty() {
        val background: Bitmap = mock()
        underTest.background = background

        val color = Color.CYAN
        underTest.backgroundColor = color

        underTest.onSurfaceCreated()

        underTest.onDrawFrame()

        verify(renderer).setBackgroundTexture(background)
        verify(renderer).setClearColor(color)

        verify(scenePresenter).draw()
        verify(scenePresenter).run()
    }

    @Test
    fun onDrawFrameWhenBackgroundAndColorNotDirty() {
        underTest.onDrawFrame()

        verify(renderer, never()).setBackgroundTexture(any())
        verify(renderer, never()).setClearColor(any())

        verify(scenePresenter).draw()
        verify(scenePresenter).run()
    }
}
