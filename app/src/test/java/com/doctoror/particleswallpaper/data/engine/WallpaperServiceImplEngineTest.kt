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

import android.app.WallpaperColors
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class WallpaperServiceImplEngineTest {

    private val service = WallpaperServiceImpl()
    private val presenter: EnginePresenter = mock()
    private val renderer: GlSceneRenderer = mock()

    private val underTest = service.EngineImpl(renderer).apply {
        presenter = this@WallpaperServiceImplEngineTest.presenter
    }

    @Test
    fun forwardsOnCreateToPresenter() {
        // When
        underTest.onCreate(null)

        // Then
        verify(presenter).onCreate()
    }

    @Test
    fun forwardsOnDestroyToPresenter() {
        // When
        underTest.onDestroy()

        // Then
        verify(presenter).onDestroy()
    }

    @Ignore("Cannot mock GL10 in Robolectric")
    @Test
    fun forwardsSurfaceChangeToPresenter() {
        // Given
        val width = 1
        val height = 2

        // When
        underTest.onSurfaceChanged(mock(), width, height)

        // Then
        verify(presenter).setDimensions(width, height)
    }

    @Test
    fun forwardsVisibilityChangeTrueToPresenter() {
        // When
        underTest.onVisibilityChanged(true)

        // Then
        verify(presenter).visible = true
    }

    @Test
    fun forwardsVisibilityChangeFalseToPresenter() {
        // When
        underTest.onVisibilityChanged(false)

        // Then
        verify(presenter).visible = false
    }

    @Test
    fun retrurnsWallpaperColorsFromPresenter() {
        // Given
        val wallpaperColors: WallpaperColors = mock()
        whenever(presenter.onComputeColors()).thenReturn(wallpaperColors)

        // When
        val result = underTest.onComputeColors()

        // Then
        assertEquals(wallpaperColors, result)
    }
}
