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

import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class WallpaperServiceImplEngineTest {

    private val configurator: SceneConfigurator = mock()
    private val glide: RequestManager = spy(Glide.with(RuntimeEnvironment.systemContext))
    private val settings: SettingsRepository = mock()
    private val service = WallpaperServiceImpl()

    private val underTest = service.EngineImpl(
            configurator, glide, TrampolineSchedulers(), settings)

    @Before
    fun setup() {
        whenever(settings.getBackgroundColor()).thenReturn(Observable.just(0))
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(NO_URI))
        whenever(settings.getFrameDelay()).thenReturn(Observable.just(0))
    }

    @After
    fun tearDown() {
        underTest.onDestroy()
    }

    @Test
    fun subscribesToConfigurator() {
        // When
        underTest.onCreate(null)

        // Then
        verify(configurator).subscribe(any(), eq(settings))
    }

    @Test
    fun clearsLastUsedImageTargetWhenUriIsLoaded() {
        // When
        underTest.onCreate(null)

        // Then
        verify(glide).clear(null)
    }

    @Test
    fun doesNotLoadUriWhenWidthOrHeightIs0() {
        // Given
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just("content://"))

        // When
        underTest.onCreate(null)

        // Then
        verify(glide, never()).load(any<Uri>())
    }

    @Test
    fun loadsUriWhenWidthOrHeightIsNot0AfterOnCreate() {
        // Given
        val uri = "content://"
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(uri))

        // When
        underTest.onCreate(null)
        underTest.onSurfaceChanged(mock(), 0, 1, 1)

        // Then
        verify(glide).load(uri)
    }

    @Test
    fun loadsUriWhenWidthOrHeightIsNot0BeforeOnCreate() {
        // Given
        val uri = "content://"
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(uri))

        // When
        underTest.onSurfaceChanged(mock(), 0, 1, 1)
        underTest.onCreate(null)

        // Then
        verify(glide).load(uri)
    }
}
