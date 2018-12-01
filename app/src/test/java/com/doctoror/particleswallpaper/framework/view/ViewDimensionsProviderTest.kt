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
package com.doctoror.particleswallpaper.framework.view

import android.view.View
import android.view.ViewTreeObserver
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

class ViewDimensionsProviderTest {

    private val viewTreeObserver: ViewTreeObserver = mock()

    private val view: View = mock {
        on { it.viewTreeObserver }.thenReturn(viewTreeObserver)
    }

    @Test
    fun notifiesViewDimensionsWhenBothAreNonZero() {
        // Given
        val width = 1
        val height = 2

        whenever(view.width).thenReturn(width)
        whenever(view.height).thenReturn(height)

        val o = ViewDimensionsProvider(view).provideDimensions().test()

        o.assertResult(Dimensions(width, height))
    }

    @Test
    fun doesNotNotifyDimensionsAndRegistersListenerWhenTheyAreZero() {
        ViewDimensionsProvider(view).provideDimensions().test()
        verify(viewTreeObserver).addOnGlobalLayoutListener(any())
    }

    @Test
    fun notifiesDimensionsWhenOnGlobalLayoutOccursAndRemovesTheListener() {
        // Given
        val captor = argumentCaptor<ViewTreeObserver.OnGlobalLayoutListener>()

        val width = 1
        val height = 2

        // When
        val o = ViewDimensionsProvider(view).provideDimensions().test()
        verify(viewTreeObserver).addOnGlobalLayoutListener(captor.capture())

        whenever(view.width).thenReturn(width)
        whenever(view.height).thenReturn(height)

        captor.firstValue.onGlobalLayout()

        // Then
        @Suppress("DEPRECATION")
        verify(viewTreeObserver).removeGlobalOnLayoutListener(captor.firstValue)

        o.assertResult(Dimensions(width, height))
    }
}
