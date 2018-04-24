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

import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class EngineViewTest {

    private val underTest = EngineView(mock())

    @Test
    fun paintStyleIsFill() {
        assertEquals(Paint.Style.FILL, underTest.backgroundPaint.style)
    }

    @Test
    fun setsBackgroundColor() {
        // Given
        val color = 666

        // When
        underTest.setBackgroundColor(color)

        // Them
        assertEquals(color, underTest.backgroundPaint.color)
    }

    @Test
    fun widthAndHeightChangedOnDimensionChange() {
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
    fun drawableBoundsChangedOnDimensionsChange() {
        // Given
        val width = 1
        val height = 2

        // When
        underTest.setDimensions(width, height)

        // Then
        assertEquals(width, underTest.drawable.bounds.width())
        assertEquals(height, underTest.drawable.bounds.height())
    }

    @Test
    fun backgroundBoundsChangedOnDimensionsChange() {
        // Given
        val width = 1
        val height = 2
        val background: Drawable = mock()
        underTest.background = background

        // When
        underTest.setDimensions(width, height)

        // Then
        verify(background).setBounds(0, 0, width, height)
    }
}
