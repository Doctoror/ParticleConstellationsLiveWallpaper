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
package com.doctoror.particleswallpaper.userprefs.resettodefaults

import android.app.Application
import android.graphics.Color
import android.net.Uri
import com.doctoror.particleswallpaper.userprefs.bgimage.ReleasePersistableUriPermissionUseCase
import com.doctoror.particleswallpaper.userprefs.data.DefaultSceneSettings
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class ResetToDefaultsUseCaseTest {

    private val defaults: DefaultSceneSettings = mock()
    private val releasePersistableUriPermissionUseCase: ReleasePersistableUriPermissionUseCase =
        mock()
    private val settings: SceneSettings = mock()
    private val settingsOpenGL: OpenGlSettings = mock()

    private val underTest = ResetToDefaultsUseCase(
        defaults, releasePersistableUriPermissionUseCase, settings, settingsOpenGL
    )

    @Before
    fun setup() {
        whenever(defaults.backgroundColor).thenReturn(0xff212121.toInt())
        whenever(defaults.backgroundUri).thenReturn(NO_URI)
        whenever(defaults.backgroundScroll).thenReturn(true)
        whenever(defaults.density).thenReturn(1)
        whenever(defaults.frameDelay).thenReturn(1)
        whenever(defaults.lineLength).thenReturn(86f)
        whenever(defaults.lineScale).thenReturn(1.1f)
        whenever(defaults.particleColor).thenReturn(Color.WHITE)
        whenever(defaults.particleScale).thenReturn(1.1f)
        whenever(defaults.particlesScroll).thenReturn(true)
        whenever(defaults.speedFactor).thenReturn(1.1f)

        whenever(settings.backgroundUri).thenReturn(NO_URI)
    }

    @Test
    fun setsDefaultBackgroundColor() {
        // When
        underTest.action().test()

        // Then
        verify(settings).backgroundColor = defaults.backgroundColor
    }

    @Test
    fun setsDefaultBackgroundScroll() {
        // When
        underTest.action().test()

        // Then
        verify(settings).backgroundScroll = defaults.backgroundScroll
    }

    @Test
    fun setsDefaultBackgroundUri() {
        val prev = "uri"
        whenever(settings.backgroundUri).thenReturn(prev)

        // When
        underTest.action().test()

        // Then
        verify(settings).backgroundUri = defaults.backgroundUri
        verify(releasePersistableUriPermissionUseCase).invoke(Uri.parse(prev))
    }

    @Test
    fun setsDefaultDensity() {
        // When
        underTest.action().test()

        // Then
        verify(settings).density = defaults.density
    }

    @Test
    fun setsDefaultFrameDelay() {
        // When
        underTest.action().test()

        // Then
        verify(settings).frameDelay = defaults.frameDelay
    }

    @Test
    fun setsDefaultLineLength() {
        // When
        underTest.action().test()

        // Then
        verify(settings).lineLength = defaults.lineLength
    }

    @Test
    fun setsDefaultLineScale() {
        // When
        underTest.action().test()

        // Then
        verify(settings).lineScale = defaults.lineScale
    }

    @Test
    fun setsDefaultParticleColor() {
        // When
        underTest.action().test()

        // Then
        verify(settings).particleColor = defaults.particleColor
    }

    @Test
    fun setsDefaultParticleScale() {
        // When
        underTest.action().test()

        // Then
        verify(settings).particleScale = defaults.particleScale
    }

    @Test
    fun setsDefaultParticlesScroll() {
        // When
        underTest.action().test()

        // Then
        verify(settings).particlesScroll = defaults.particlesScroll
    }

    @Test
    fun setsDefaultSpeedFactor() {
        // When
        underTest.action().test()

        // Then
        verify(settings).speedFactor = defaults.speedFactor
    }

    @Test
    fun resetsOpenGLSettingsToDefault() {
        // When
        underTest.action().test()

        // Then
        verify(settingsOpenGL).resetToDefaults()
    }
}
