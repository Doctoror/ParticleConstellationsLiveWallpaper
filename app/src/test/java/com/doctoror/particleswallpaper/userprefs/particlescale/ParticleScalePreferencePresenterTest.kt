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
package com.doctoror.particleswallpaper.userprefs.particlescale

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.userprefs.data.MutableSettingsRepository
import com.doctoror.particleswallpaper.framework.preference.SeekBarPreferenceView
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class ParticleScalePreferencePresenterTest {

    private val settings: MutableSettingsRepository = mock()
    private val view: SeekBarPreferenceView = mock()

    private val underTest = ParticleScalePreferencePresenter(TrampolineSchedulers(), settings)

    @Test
    fun testMapper() {
        com.doctoror.particleswallpaper.framework.preference.testMapper(underTest)
    }

    @Test
    fun setsMaxValueOnTakeView() {
        // When
        underTest.onTakeView(view)

        // Then
        verify(view).setMaxInt(70)
    }

    @Test
    fun setsDotScaleOnStart() {
        // Given
        val scale = 1.1f
        whenever(settings.getDotScale()).thenReturn(Observable.just(scale))
        underTest.onTakeView(view)

        // When
        underTest.onStart()

        // Then
        verify(view).setProgressInt(((scale - 0.5f) * 5f).toInt())
    }

    @Test
    fun doesNotSetDotScaleAfterOnStop() {
        // Given
        val dotScaleSource = PublishSubject.create<Float>()
        whenever(settings.getDotScale()).thenReturn(dotScaleSource)
        underTest.onTakeView(view)

        // When
        underTest.onStart()
        underTest.onStop()
        dotScaleSource.onNext(1f)

        // Then
        verify(view, never()).setProgressInt(any())
    }

    @Test
    fun storesDotScaleOnChange() {
        // Given
        val progress = 1

        // When
        underTest.onPreferenceChange(progress)

        // Then
        verify(settings).setDotScale(progress.toFloat() / 5f + 0.5f)
    }
}
