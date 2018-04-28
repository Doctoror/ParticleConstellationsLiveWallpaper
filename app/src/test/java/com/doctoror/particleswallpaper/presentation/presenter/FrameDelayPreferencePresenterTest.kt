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

import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.presentation.view.SeekBarPreferenceView
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class FrameDelayPreferencePresenterTest {

    private val frameDelaySeekbarMin = 10

    private val settings: MutableSettingsRepository = mock()
    private val view: SeekBarPreferenceView = mock {
        on(it.getMaxInt()).doReturn(80)
    }

    private val underTest = FrameDelayPreferencePresenter(TrampolineSchedulers(), settings)

    @Test
    fun setsMaxValueOnTakeView() {
        // When
        underTest.onTakeView(view)

        // Then
        verify(view).setMaxInt(80)
    }

    @Test
    fun setsFrameDelayOnStart() {
        // Given
        val frameDelay = 9
        whenever(settings.getFrameDelay()).thenReturn(Observable.just(frameDelay))
        underTest.onTakeView(view)

        // When
        underTest.onStart()

        // Then
        val progress = transformToProgress(frameDelay)
        verify(view).setProgressInt(progress)
    }

    @Test
    fun doesNotSetFrameDelayAfterOnStop() {
        // Given
        val frameDelaySource = PublishSubject.create<Int>()
        whenever(settings.getFrameDelay()).thenReturn(frameDelaySource)
        underTest.onTakeView(view)

        // When
        underTest.onStart()
        underTest.onStop()
        frameDelaySource.onNext(1)

        // Then
        verify(view, never()).setProgressInt(any())
    }

    @Test
    fun storesFrameDelayOnChange() {
        // Given
        val progress = 80

        // When
        underTest.onTakeView(view)
        underTest.onPreferenceChange(progress)

        // Then
        verify(settings).setFrameDelay(transformToRealValue(progress))
    }

    private fun transformToProgress(value: Int): Int {
        val percent = (value.toFloat() - frameDelaySeekbarMin.toFloat()) / view.getMaxInt().toFloat()
        return ((1f - percent) * view.getMaxInt().toFloat()).toInt()
    }

    private fun transformToRealValue(progress: Int) = (frameDelaySeekbarMin.toFloat()
            + view.getMaxInt().toFloat() * (1f - progress.toFloat() / view.getMaxInt().toFloat())).toInt()
}
