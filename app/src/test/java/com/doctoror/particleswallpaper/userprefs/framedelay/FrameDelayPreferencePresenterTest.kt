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
package com.doctoror.particleswallpaper.userprefs.framedelay

import android.content.Context
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.view.DisplayFrameRateProvider
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

class FrameDelayPreferencePresenterTest {

    private val context: Context = mock()

    private val displayFrameRateProvider: DisplayFrameRateProvider = mock()

    private val frameDelaySeekbarMin = 16

    private val settings: SceneSettings = mock()

    private val view: FrameDelayPreferenceView = mock {
        on(it.getMaxInt()).doReturn(25)
    }

    private val underTest = FrameDelayPreferencePresenter(
        context,
        displayFrameRateProvider,
        TrampolineSchedulers(),
        settings,
        view
    )

    @Test
    fun testMapper() {
        com.doctoror.particleswallpaper.framework.preference.testMapper(underTest)
    }

    @Test
    fun testMinValue() {
        assertEquals(0, transformToRealValue(underTest.getSeekbarMax()))
    }

    @Test
    fun setsMaxValueOnTakeView() {
        verify(view).setMaxInt(25)
    }

    @Test
    fun setsFrameDelayOnStart() {
        // Given
        val frameDelay = 16
        whenever(settings.observeFrameDelay()).thenReturn(Observable.just(frameDelay))

        // When
        underTest.onStart()

        // Then
        val progress = transformToProgress(frameDelay)
        verify(view).setProgressInt(progress)
    }

    @Test
    fun updatesFrameRateOnStart() {
        // Given
        val frameRate = 60
        whenever(displayFrameRateProvider.provide(context)).thenReturn(frameRate)

        val frameDelay = 16
        whenever(settings.observeFrameDelay()).thenReturn(Observable.just(frameDelay))

        // When
        underTest.onStart()

        // Then
        verify(view).setFrameRate(frameRate)
    }

    @Test
    fun doesNotSetFrameDelayAfterOnStop() {
        // Given
        val frameDelaySource = PublishSubject.create<Int>()
        whenever(settings.observeFrameDelay()).thenReturn(frameDelaySource)

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
        val progress = 25

        // When
        underTest.onPreferenceChange(progress)

        // Then
        verify(settings).frameDelay = transformToRealValue(progress)
    }

    @Test
    fun notifiesFrameRateOnChange() {
        // Given
        val frameRate = 60
        whenever(displayFrameRateProvider.provide(context)).thenReturn(frameRate)

        val progress = 25

        // When
        underTest.onPreferenceChange(progress)

        // Then
        verify(view).setFrameRate(frameRate)
    }

    private fun transformToProgress(value: Int): Int {
        val percent =
            (value.toFloat() - frameDelaySeekbarMin.toFloat()) / view.getMaxInt().toFloat()
        return ((1f - percent) * view.getMaxInt().toFloat()).toInt()
    }

    private fun transformToRealValue(progress: Int): Int {
        var value = (frameDelaySeekbarMin.toFloat() + view.getMaxInt().toFloat() *
                (1f - progress.toFloat() / view.getMaxInt().toFloat())).toInt()
        if (value <= 16) {
            value = 0
        }
        return value
    }
}
