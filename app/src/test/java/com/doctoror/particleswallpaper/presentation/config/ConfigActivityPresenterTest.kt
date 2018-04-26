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
package com.doctoror.particleswallpaper.presentation.config

import android.app.Activity
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestManager
import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_CHANGE_WALLPAPER
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ConfigActivityPresenterTest {

    private val configurator: SceneConfigurator = mock()
    private val settings: SettingsRepository = mock()
    private val requestManager: RequestManager = mock()
    private val view: ConfigActivityView = mock()

    private val underTest = ConfigActivityPresenter(
            TrampolineSchedulers(), configurator, requestManager, settings)

    @Test
    fun setsBackground() {
        // When
        underTest.onTakeView(view)

        // Then
        val captor = argumentCaptor<Drawable>()
        verify(view).setContainerBackground(captor.capture())

        assertTrue(captor.firstValue is ParticlesDrawable)
    }

    @Test
    fun finishesWhenWallpaperSet() {
        // Given
        underTest.onTakeView(view)

        // When
        underTest.onActivityResult(REQUEST_CODE_CHANGE_WALLPAPER, Activity.RESULT_OK)

        // Then
        verify(view).finish()
    }

    @Test
    fun doesNotFinishWhenWallpaperNotSet() {
        // Given
        underTest.onTakeView(view)

        // When
        underTest.onActivityResult(REQUEST_CODE_CHANGE_WALLPAPER, Activity.RESULT_CANCELED)

        // Then
        verify(view, never()).finish()
    }
}
