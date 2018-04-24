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

import android.net.Uri
import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class EnginePresenterTest {

    private val configurator: SceneConfigurator = mock()
    private val controller: EngineController = mock()
    private val glide: RequestManager = spy(Glide.with(RuntimeEnvironment.systemContext))
    private val settings: SettingsRepository = mock()
    private val view: EngineView = mock()
    private val apiLevelProvider: ApiLevelProvider = mock()

    private val underTest = EnginePresenter(
            configurator,
            controller,
            glide,
            TrampolineSchedulers(),
            settings,
            view,
            apiLevelProvider)

    @Before
    fun setup() {
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION.SDK_INT)
        whenever(settings.getBackgroundColor()).thenReturn(Observable.just(0))
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(NO_URI))
        whenever(settings.getFrameDelay()).thenReturn(Observable.just(0))
    }

    @After
    fun tearDown() {
        underTest.onDestroy()
    }

    @Test
    fun subscribesToConfigurator() {
        // Given
        val drawable: ParticlesDrawable = mock()
        whenever(view.drawable).thenReturn(drawable)

        // When
        underTest.onCreate()

        // Then
        verify(configurator).subscribe(drawable, settings)
    }

    @Test
    fun loadsBackgroundColor() {
        // Given
        val color = 666
        whenever(settings.getBackgroundColor()).thenReturn(Observable.just(color))

        // When
        underTest.onCreate()

        // Then
        verify(view).setBackgroundColor(color)
    }

    @Test
    fun loadsBackgroundUri() {
        // Given
        val uri = "uri://scheme"
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(uri))

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
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just("content://"))

        // When
        underTest.onCreate()

        // Then
        verify(glide, never()).load(any<Uri>())
    }

    @Test
    fun loadsUriWhenWidthOrHeightIsNot0AfterOnCreate() {
        // Given
        val uri = "content://"
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(uri))

        // When
        underTest.onCreate()
        underTest.setDimensions(1, 1)

        // Then
        verify(glide).load(uri)
    }

    @Test
    fun loadsUriWhenWidthOrHeightIsNot0BeforeOnCreate() {
        // Given
        val uri = "content://"
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(uri))

        // When
        underTest.setDimensions(1, 1)
        underTest.onCreate()

        // Then
        verify(glide).load(uri)
    }

    @Test
    fun loadsFrameDelay() {
        // Given
        val frameDelay = 666
        whenever(settings.getFrameDelay()).thenReturn(Observable.just(frameDelay))

        // When
        underTest.onCreate()

        // Then
        assertEquals(frameDelay.toLong(), underTest.delay)
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
    fun drawableBoundsChangedOnSurfaceChange() {
        // Given
        val width = 1
        val height = 2

        // When
        underTest.setDimensions(width, height)

        // Then
        verify(view).setDimensions(width, height)
    }

    @Test
    fun startsWhenVisible() {
        // When
        underTest.visible = true

        // Then
        verify(view).start()
    }

    @Test
    fun stopsWhenNotVisible() {
        // Given
        underTest.visible = true

        // When
        underTest.visible = false

        // Then
        verify(view).stop()
    }
}
