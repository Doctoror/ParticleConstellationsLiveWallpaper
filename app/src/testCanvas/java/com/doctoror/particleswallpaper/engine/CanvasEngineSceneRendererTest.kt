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
package com.doctoror.particleswallpaper.engine

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.renderer.CanvasSceneRenderer
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CanvasEngineSceneRendererTest {

    private val canvasSceneRenderer: CanvasSceneRenderer = mock()
    private val resources = mockResourcesWithDisplayMetrics()

    private val underTest = CanvasEngineSceneRenderer(canvasSceneRenderer, resources)

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun backgroundSet() {
        val background: Bitmap = mock()

        underTest.setBackgroundTexture(background)

        val actual = underTest.background
        assertTrue(background === (actual as BitmapDrawable).bitmap)
    }

    @Test
    fun backgroundBountsSetWithDimensions() {
        val width = 2
        val height = 4

        underTest.setBackgroundTexture(mock())
        underTest.setDimensions(width, height)

        assertEquals(width, underTest.background!!.bounds.width())
        assertEquals(height, underTest.background!!.bounds.height())
    }

    @Test
    fun backgroundBountsAppliedFromDimensions() {
        val width = 2
        val height = 4

        underTest.setDimensions(width, height)
        underTest.setBackgroundTexture(mock())

        assertEquals(width, underTest.background!!.bounds.width())
        assertEquals(height, underTest.background!!.bounds.height())
    }

    @Test
    fun resetsSurfaceHolderCacheOnDimensionsChange() {
        underTest.surfaceHolder = mock()

        underTest.setDimensions(0, 0)

        assertNull(underTest.surfaceHolder)
    }

    @Test
    fun resetsSurfaceHolderCacheOnRequest() {
        underTest.surfaceHolder = mock()

        underTest.resetSurfaceCache()

        assertNull(underTest.surfaceHolder)
    }

    @Test
    fun resetsSurfaceHolderCacheOnRecycle() {
        underTest.surfaceHolder = mock()

        underTest.recycle()

        assertNull(underTest.surfaceHolder)
    }

    @Test
    fun resetsBackgroundOnRecycle() {
        underTest.background = mock()

        underTest.recycle()

        assertNull(underTest.background)
    }

    @Test
    fun resetsBackgroundWhenNullPassed() {
        underTest.background = mock()
        underTest.setBackgroundTexture(null)

        underTest.recycle()

        assertNull(underTest.background)
    }

    @Test(expected = IllegalStateException::class)
    fun drawSceneThrowsWhenNoSurfaceHolder() {
        underTest.drawScene(ParticlesScene())
    }

    @Test
    fun drawsEmptyScene() {
        // Given
        val canvas: Canvas = mock()

        val surfaceHolder: SurfaceHolder = mock {
            on { it.lockCanvas() }.thenReturn(canvas)
            on { it.lockHardwareCanvas() }.thenReturn(canvas)
        }

        underTest.surfaceHolderProvider = mock {
            on { it.provideSurfaceHolder() }.thenReturn(surfaceHolder)
        }

        val backgroundColor = Color.CYAN
        underTest.setClearColor(backgroundColor)

        val width = 1
        val height = 2
        underTest.setDimensions(width, height)

        // When
        underTest.drawScene(ParticlesScene())

        // Then

        // Verify background color drawn
        val paintCaptor = argumentCaptor<Paint>()
        verify(canvas).drawRect(
            eq(0f),
            eq(0f),
            eq(width.toFloat()),
            eq(height.toFloat()),
            paintCaptor.capture()
        )
        assertEquals(backgroundColor, paintCaptor.firstValue.color)

        // Verify content drawn
        verify(canvasSceneRenderer).setCanvas(canvas)
        verify(canvasSceneRenderer).setCanvas(null)

        verify(surfaceHolder).unlockCanvasAndPost(canvas)
    }

    private fun mockResourcesWithDisplayMetrics(): Resources {
        val displayMetrics: DisplayMetrics = mock()
        return mock {
            on { it.displayMetrics }.thenReturn(displayMetrics)
        }
    }
}
