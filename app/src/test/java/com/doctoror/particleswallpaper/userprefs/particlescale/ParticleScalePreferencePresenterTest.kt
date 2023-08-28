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
import com.doctoror.particleswallpaper.framework.preference.SeekBarPreferenceView
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.kotlin.*

class ParticleScalePreferencePresenterTest {

    private val settings: SceneSettings = mock()
    private val view: SeekBarPreferenceView = mock()

    private val underTest = ParticleScalePreferencePresenter(TrampolineSchedulers(), settings, view)

    @Test
    fun testMapper() {
        com.doctoror.particleswallpaper.framework.preference.testMapper(underTest)
    }

    @Test
    fun setsMaxValueOnInit() {
        verify(view).setMaxInt(140)
    }

    @Test
    fun setsValueOnStart() {
        // Given
        val scale = 1.1f
        whenever(settings.observeParticleScale()).thenReturn(Observable.just(scale))

        // When
        underTest.onStart()

        // Then
        verify(view).setProgressInt(((scale - 0.5f) * 5f).toInt())
    }

    @Test
    fun doesNotSetValueAfterOnStop() {
        // Given
        val particleScaleSource = PublishSubject.create<Float>()
        whenever(settings.observeParticleScale()).thenReturn(particleScaleSource)

        // When
        underTest.onStart()
        underTest.onStop()
        particleScaleSource.onNext(1f)

        // Then
        verify(view, never()).setProgressInt(any())
    }

    @Test
    fun storesValueOnChange() {
        // Given
        val progress = 1

        // When
        underTest.onPreferenceChange(progress)

        // Then
        verify(settings).particleScale = progress.toFloat() / 5f + 0.5f
    }
}
