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
package com.doctoror.particleswallpaper.presentation.util

import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ThemeUtilsTest {

    private val attributes: TypedArray = mock()

    private val theme: Resources.Theme = mock {
        on(it.obtainStyledAttributes(intArrayOf(1))).doReturn(attributes)
    }

    @Test
    fun obtainsColorFromTheme() {
        // Given
        val attr = 1
        val expectedResult = Color.CYAN
        whenever(attributes.getColor(0, Color.TRANSPARENT)).thenReturn(expectedResult)

        // When
        val result = ThemeUtils.getColor(theme, attr)

        // Then
        assertEquals(expectedResult, result)
    }
}
