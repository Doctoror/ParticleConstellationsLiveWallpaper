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

import android.graphics.Color
import com.doctoror.particleswallpaper.framework.file.BackgroundImageManager
import com.doctoror.particleswallpaper.userprefs.data.DefaultSceneSettings
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.jupiter.api.Test

class ResetToDefaultsUseCaseTest {

    private val defaults: DefaultSceneSettings = mock {
        on(it.backgroundColor).doReturn(0xff212121.toInt())
        on(it.backgroundUri).doReturn(NO_URI)
        on(it.density).doReturn(1)
        on(it.frameDelay).doReturn(1)
        on(it.lineLength).doReturn(86f)
        on(it.lineScale).doReturn(1.1f)
        on(it.particleColor).doReturn(Color.WHITE)
        on(it.particleScale).doReturn(1.1f)
        on(it.speedFactor).doReturn(1.1f)
    }

    private val settings: SceneSettings = mock()
    private val settingsOpenGL: OpenGlSettings = mock()
    private val backgroundImageManager: BackgroundImageManager = mock()

    private val underTest = ResetToDefaultsUseCase(
            defaults, settings, settingsOpenGL, backgroundImageManager)

    @Test
    fun setsDefaultBackgroundColor() {
        // When
        underTest.action().test()

        // Then
        verify(settings).backgroundColor = defaults.backgroundColor
    }

    @Test
    fun setsDefaultBackgroundUri() {
        // When
        underTest.action().test()

        // Then
        verify(settings).backgroundUri = defaults.backgroundUri
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
    fun setsDefaultSpeedFactor() {
        // When
        underTest.action().test()

        // Then
        verify(settings).speedFactor = defaults.speedFactor
    }

    @Test
    fun clearsBackgroundImage() {
        // When
        underTest.action().test()

        // Then
        verify(backgroundImageManager).clearBackgroundImage()
    }

    @Test
    fun resetsOpenGLSettingsToDefault() {
        // When
        underTest.action().test()

        // Then
        verify(settingsOpenGL).resetToDefaults()
    }
}
