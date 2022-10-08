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
package com.doctoror.particleswallpaper.userprefs.speedfactor

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.preference.SeekBarPreferenceView
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import org.mockito.kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class SpeedFactorPreferencePresenterTest {

    private val settings: SceneSettings = mock()
    private val view: SeekBarPreferenceView = mock()

    private val underTest = SpeedFactorPreferencePresenter(TrampolineSchedulers(), settings, view)

    @Test
    fun testMapper() {
        com.doctoror.particleswallpaper.framework.preference.testMapper(underTest)
    }

    @Test
    fun setsMaxValueOnInit() {
        verify(view).setMaxInt(40)
    }

    @Test
    fun setsSpeedFactorOnStart() {
        // Given
        val speedFactor = 1.5f
        whenever(settings.observeSpeedFactor()).thenReturn(Observable.just(speedFactor))

        // When
        underTest.onStart()

        // Then
        verify(view).setProgressInt(((speedFactor - 0.1f) * 10f).toInt())
    }

    @Test
    fun doesNotSetSpeedFactorAfterOnStop() {
        // Given
        val speedFactorSource = PublishSubject.create<Float>()
        whenever(settings.observeSpeedFactor()).thenReturn(speedFactorSource)

        // When
        underTest.onStart()
        underTest.onStop()
        speedFactorSource.onNext(1f)

        // Then
        verify(view, never()).setProgressInt(any())
    }

    @Test
    fun storesSpeedFactorOnChange() {
        // Given
        val progress = 10

        // When
        underTest.onPreferenceChange(progress)

        // Then
        verify(settings).speedFactor = progress.toFloat() / 10f + 0.1f
    }
}
