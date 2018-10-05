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
package com.doctoror.particleswallpaper.settings

import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import com.doctoror.particleswallpaper.R
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Test

class SettingsRepositoryDefaultTest {

    private val res: Resources = mock()
    private val theme: Resources.Theme = mock()
    private val typedValueFactory: SettingsRepositoryDefault.TypedValueFactory = mock()

    private val underTest = SettingsRepositoryDefault(res, theme, typedValueFactory)

    @Test
    fun obtainsBackgroundColorFromResources() {
        // Given
        val value = Color.DKGRAY
        @Suppress("DEPRECATION")
        whenever(res.getColor(R.color.defaultBackground)).thenReturn(value)
        whenever(res.getColor(R.color.defaultBackground, theme)).thenReturn(value)

        // When
        val o = underTest.getBackgroundColor().test()

        // Then
        o.assertResult(value)
    }

    @Test
    fun backgroundUriIsNoUri() {
        // When
        val o = underTest.getBackgroundUri().test()

        // Then
        o.assertResult(NO_URI)
    }

    @Test
    fun obtainsDotScaleFromResources() {
        // Given
        val value = 0.6f
        whenever(res.getDimension(R.dimen.default_dot_scale)).thenReturn(value)

        // When
        val o = underTest.getDotScale().test()

        // Then
        o.assertResult(value)
    }

    @Test
    fun doesNotReturnDotScaleLessThanHalf() {
        // Given
        val value = 0.49f
        whenever(res.getDimension(R.dimen.default_dot_scale)).thenReturn(value)

        // When
        val o = underTest.getDotScale().test()

        // Then
        o.assertResult(0.5f)
    }

    @Test
    fun obtainsFrameDelayFromResources() {
        // Given
        val value = 10
        whenever(res.getInteger(R.integer.defaultFrameDelay)).thenReturn(value)

        // When
        val o = underTest.getFrameDelay().test()

        // Then
        o.assertResult(value)
    }

    @Test
    fun obtainsLineDistanceFromResources() {
        // Given
        val value = 1.1f
        whenever(res.getDimension(R.dimen.default_line_distance)).thenReturn(value)

        // When
        val o = underTest.getLineDistance().test()

        // Then
        o.assertResult(value)
    }

    @Test
    fun obtainsLineScaleFromResources() {
        // Given
        val value = 1.1f
        whenever(res.getDimension(R.dimen.default_line_scale)).thenReturn(value)

        // When
        val o = underTest.getLineScale().test()

        // Then
        o.assertResult(value)
    }

    @Test
    fun doesNotReturnLineScaleLessThan1() {
        // Given
        val value = 0.99f
        whenever(res.getDimension(R.dimen.default_line_scale)).thenReturn(value)

        // When
        val o = underTest.getLineScale().test()

        // Then
        o.assertResult(1f)
    }

    @Test
    fun obtainsNumDotsFromResources() {
        // Given
        val value = 2
        whenever(res.getInteger(R.integer.default_density)).thenReturn(value)

        // When
        val o = underTest.getNumDots().test()

        // Then
        o.assertResult(value)
    }

    @Test
    fun obtainsParticlesColorFromResources() {
        // Given
        val value = Color.CYAN
        @Suppress("DEPRECATION")
        whenever(res.getColor(R.color.defaultParticlesColor)).thenReturn(value)
        whenever(res.getColor(R.color.defaultParticlesColor, theme)).thenReturn(value)

        // When
        val o = underTest.getParticlesColor().test()

        // Then
        o.assertResult(value)
    }

    @Test
    fun obtainsStepMultiplierFromResources() {
        // Given
        val value = 1.1f
        val typedValue: TypedValue = mock {
            on(it.float).doReturn(value)
        }
        whenever(typedValueFactory.newTypedValue()).thenReturn(typedValue)

        // When
        val o = underTest.getStepMultiplier().test()

        // Then
        verify(res).getValue(R.dimen.defaultStepMultiplier, typedValue, true)
        o.assertResult(value)
    }
}
