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

import android.app.WallpaperColors
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer
import com.doctoror.particleswallpaper.engine.opengl.GlWallpaperServiceImpl
import com.doctoror.particleswallpaper.framework.util.KnownOpenglIssuesHandler
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WallpaperServiceImplEngineTest {

    private val service = GlWallpaperServiceImpl()
    private val presenter: EnginePresenter = mock()
    private val knownOpenglIssuesHandler: KnownOpenglIssuesHandler = mock()
    private val renderer: GlSceneRenderer = mock()

    private val underTest = service.EngineImpl(knownOpenglIssuesHandler, renderer, 0).apply {
        presenter = this@WallpaperServiceImplEngineTest.presenter
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
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

    @Test
    fun forwardsSurfaceChangeToPresenter() {
        // Given
        val width = 1
        val height = 2
        val desiredWidth = 3
        val desiredHeight = 4

        // Must spy to mock desiredMinimumWidth / desiredMinimumHeight, otherwise it crashes.
        val underTest = spy(underTest).apply {
            doReturn(desiredWidth).`when`(this).desiredMinimumWidth
            doReturn(desiredHeight).`when`(this).desiredMinimumHeight
        }

        // When
        underTest.onSurfaceChanged(mock(), width, height)

        // Then
        verify(presenter).setDimensions(
            EnginePresenter.WallpaperDimensions(
                width,
                height,
                desiredWidth,
                desiredHeight
            )
        )
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

    @Test
    fun drawsFrameAndHandlesKnownIssues() {
        underTest.onDrawFrame(mock())

        val inorder = inOrder(presenter, knownOpenglIssuesHandler)
        inorder.verify(presenter).onDrawFrame()
        inorder.verify(knownOpenglIssuesHandler).handle("GlWallpaperServiceImpl.onDrawFrame")
    }
}
