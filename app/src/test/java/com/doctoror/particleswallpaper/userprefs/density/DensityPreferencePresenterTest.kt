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
package com.doctoror.particleswallpaper.userprefs.density

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.preference.SeekBarPreferenceView
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class DensityPreferencePresenterTest {

    private val settings: SceneSettings = mock()
    private val view: SeekBarPreferenceView = mock()

    private val underTest = DensityPreferencePresenter(TrampolineSchedulers(), settings)

    @Test
    fun testMapper() {
        com.doctoror.particleswallpaper.framework.preference.testMapper(underTest)
    }

    @Test
    fun setsMaxValueOnTakeView() {
        // When
        underTest.onTakeView(view)

        // Then
        verify(view).setMaxInt(149)
    }

    @Test
    fun setsNumDotsOnStart() {
        // Given
        val numDots = 128
        whenever(settings.observeDensity()).thenReturn(Observable.just(numDots))
        underTest.onTakeView(view)

        // When
        underTest.onStart()

        // Then
        verify(view).setProgressInt(numDots - 1)
    }

    @Test
    fun doesNotSetNumDotsAfterOnStop() {
        // Given
        val numDotsSource = PublishSubject.create<Int>()
        whenever(settings.observeDensity()).thenReturn(numDotsSource)
        underTest.onTakeView(view)

        // When
        underTest.onStart()
        underTest.onStop()
        numDotsSource.onNext(1)

        // Then
        verify(view, never()).setProgressInt(any())
    }

    @Test
    fun storesNumDotsOnChange() {
        // Given
        val progress = 1

        // When
        underTest.onPreferenceChange(progress)

        // Then
        verify(settings).density = progress + 1
    }
}
