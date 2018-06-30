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
package com.doctoror.particleswallpaper.presentation.presenter

import android.graphics.Color
import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.view.BackgroundColorPreferenceView
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class BackgroundColorPreferencePresenterTest {

    private val settings: MutableSettingsRepository = mock()
    private val defaults: SettingsRepository = mock()
    private val view: BackgroundColorPreferenceView = mock()

    private val underTest = BackgroundColorPreferencePresenter(
            TrampolineSchedulers(), settings, defaults)

    @Test
    fun setsColorOnStart() {
        // Given
        val color = Color.CYAN
        whenever(settings.getBackgroundColor()).thenReturn(Observable.just(color))
        underTest.onTakeView(view)

        // When
        underTest.onStart()

        // Then
        verify(view).setColor(color)
    }

    @Test
    fun doesNotSetColorAfterOnStop() {
        // Given
        val colorSource = PublishSubject.create<Int>()
        whenever(settings.getBackgroundColor()).thenReturn(colorSource)
        underTest.onTakeView(view)

        // When
        underTest.onStart()
        underTest.onStop()
        colorSource.onNext(Color.BLACK)

        // Then
        verify(view, never()).setColor(any())
    }

    @Test
    fun storesColorOnChange() {
        // Given
        val color = Color.WHITE

        // When
        underTest.onPreferenceChange(color)

        // Then
        verify(settings).setBackgroundColor(color)
    }

    @Test
    fun storesDefaultColorOnNull() {
        // Given
        val defaultColor = Color.DKGRAY
        whenever(defaults.getBackgroundColor()).thenReturn(Observable.just(defaultColor))

        // When
        underTest.onPreferenceChange(null)

        // Then
        verify(settings).setBackgroundColor(defaultColor)
    }
}
