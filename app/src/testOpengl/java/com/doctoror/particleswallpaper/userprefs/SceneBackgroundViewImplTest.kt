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
import android.graphics.drawable.Drawable
import android.view.View
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SceneBackgroundViewImplTest {

    private val particlesView: View = mock()

    private val underTest = SceneBackgroundViewImpl().apply {
        particlesView = this@SceneBackgroundViewImplTest.particlesView
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun setsBackgroundColor() {
        val color = Color.CYAN

        underTest.displayBackgroundColor(color)

        verify(particlesView).setBackgroundColor(color)
    }

    @Test
    fun setsBackgroundBitmapAsNull() {
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
}
