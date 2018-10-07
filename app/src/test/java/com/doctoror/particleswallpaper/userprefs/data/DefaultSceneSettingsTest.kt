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
package com.doctoror.particleswallpaper.userprefs.data

import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import com.doctoror.particleswallpaper.R
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DefaultSceneSettingsTest {

    private val res: Resources = mock()
    private val theme: Resources.Theme = mock()
    private val typedValueFactory: DefaultSceneSettings.TypedValueFactory = mock()

    @Test
    fun obtainsBackgroundColorFromResources() {
        // Given
        val value = Color.DKGRAY
        @Suppress("DEPRECATION")
        whenever(res.getColor(R.color.defaultBackground)).thenReturn(value)
        whenever(res.getColor(R.color.defaultBackground, theme)).thenReturn(value)

        // When
        val underTest = newUnderTestInstance()

        // Then
        assertEquals(value, underTest.backgroundColor)
    }

    @Test
    fun backgroundUriIsNoUri() {
        val underTest = DefaultSceneSettings(res, theme, typedValueFactory)
        assertEquals(NO_URI, underTest.backgroundUri)
    }

    @Test
    fun obtainsParticleScaleFromResources() {
        // Given
        val value = 0.6f
        whenever(res.getDimension(R.dimen.defaultParticleScale)).thenReturn(value)

        // When
        val underTest = newUnderTestInstance()

        // Then
        assertEquals(value, underTest.particleScale)
    }

    @Test
    fun doesNotReturnParticleScaleLessThanHalf() {
        // Given
        val value = 0.49f
        whenever(res.getDimension(R.dimen.defaultParticleScale)).thenReturn(value)

        // When
        val underTest = newUnderTestInstance()

        // Then
        assertEquals(0.5f, underTest.particleScale)
    }

    @Test
    fun obtainsFrameDelayFromResources() {
        // Given
        val value = 10
        whenever(res.getInteger(R.integer.defaultFrameDelay)).thenReturn(value)

        // When
        val underTest = newUnderTestInstance()

        // Then
        assertEquals(value, underTest.frameDelay)
    }

    @Test
    fun obtainsLineLengthFromResources() {
        // Given
        val value = 1.1f
        whenever(res.getDimension(R.dimen.defaultLineLength)).thenReturn(value)

        // When
        val underTest = newUnderTestInstance()

        // Then
        assertEquals(value, underTest.lineLength)
    }

    @Test
    fun obtainsLineScaleFromResources() {
        // Given
        val value = 1.1f
        whenever(res.getDimension(R.dimen.defaultLineScale)).thenReturn(value)

        // When
        val underTest = newUnderTestInstance()

        // Then
        assertEquals(value, underTest.lineScale)
    }

    @Test
    fun doesNotReturnLineScaleLessThan1() {
        // Given
        val value = 0.99f
        whenever(res.getDimension(R.dimen.defaultLineScale)).thenReturn(value)

        // When
        val underTest = newUnderTestInstance()

        // Then
        assertEquals(1f, underTest.lineScale)
    }

    @Test
    fun obtainsDensityFromResources() {
        // Given
        val value = 2
        whenever(res.getInteger(R.integer.defaultDensity)).thenReturn(value)

        // When
        val underTest = newUnderTestInstance()

        // Then
        assertEquals(value, underTest.density)
    }

    @Test
    fun obtainsParticleColorFromResources() {
        // Given
        val value = Color.CYAN
        @Suppress("DEPRECATION")
        whenever(res.getColor(R.color.defaultParticleColor)).thenReturn(value)
        whenever(res.getColor(R.color.defaultParticleColor, theme)).thenReturn(value)

        // When
        val underTest = newUnderTestInstance()

        // Then
        assertEquals(value, underTest.particleColor)
    }

    @Test
    fun obtainsSpeedFactorFromResources() {
        // Given
        val value = 1.1f
        val typedValue: TypedValue = mock {
            on(it.float).doReturn(value)
        }
        whenever(typedValueFactory.newTypedValue()).thenReturn(typedValue)

        // When
        val underTest = newUnderTestInstance()

        // Then
        verify(res).getValue(R.dimen.defaultSpeedFactor, typedValue, true)
        assertEquals(value, underTest.speedFactor)
    }

    private fun newUnderTestInstance() = DefaultSceneSettings(res, theme, typedValueFactory)
}
