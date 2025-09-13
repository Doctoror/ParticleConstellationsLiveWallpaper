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
package com.doctoror.particleswallpaper.userprefs

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.Window
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SceneBackgroundViewTest {

    private val deviceSettings: DeviceSettings = mock {
        on(it.openglEnabled).thenReturn(true)
    }

    private val window: Window = mock()

    private val particlesView: View = mock()

    private val underTest = SceneBackgroundView(deviceSettings) { window }.apply {
        particlesView = this@SceneBackgroundViewTest.particlesView
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun setsGlBackgroundColor() {
        val color = Color.CYAN

        underTest.displayBackgroundColor(color)

        verify(particlesView).setBackgroundColor(color)
    }

    @Test
    fun setsGlBackgroundBitmapAsNull() {
        underTest.displayBackground(null)

        verify(particlesView).background = null
    }

    @Test
    fun setsBackgroundBitmap() {
        val background: Bitmap = mock()

        underTest.displayBackground(background)

        val captor = argumentCaptor<Drawable>()
        verify(particlesView).background = captor.capture()
        assertEquals(background, (captor.firstValue as BitmapDrawable).bitmap)
    }

    @Test
    fun setsBackgroundColorToWindowWhenOpenGlDisabled() {
        whenever(deviceSettings.openglEnabled).thenReturn(false)
        val color = Color.CYAN

        underTest.displayBackgroundColor(color)

        val captor = argumentCaptor<Drawable>()
        verify(window).setBackgroundDrawable(captor.capture())

        assertEquals(color, (captor.firstValue as ColorDrawable).color)
    }

    @Test
    fun setsBackgroundBitmapAsNullWhenOpenGlDisabled() {
        whenever(deviceSettings.openglEnabled).thenReturn(false)

        underTest.displayBackground(null)

        verify(particlesView).background = null
    }
}
