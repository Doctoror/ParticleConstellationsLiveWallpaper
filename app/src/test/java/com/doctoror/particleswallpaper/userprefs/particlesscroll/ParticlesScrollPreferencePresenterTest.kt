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
package com.doctoror.particleswallpaper.userprefs.particlesscroll

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import org.mockito.kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class ParticlesScrollPreferencePresenterTest {

    private val settings: SceneSettings = mock()

    private val view: ParticlesScrollPreferenceView = mock()

    private val underTest =
        ParticlesScrollPreferencePresenter(TrampolineSchedulers(), settings, view)

    @Test
    fun setsParticlesScrollOnStart() {
        // Given
        val scroll = true
        whenever(settings.observeParticlesScroll()).thenReturn(Observable.just(scroll))

        // When
        underTest.onStart()

        // Then
        verify(view).setChecked(scroll)
    }

    @Test
    fun doesNotSetParticlesScrollAfterOnStop() {
        // Given
        val particlesScrollSource = PublishSubject.create<Boolean>()
        whenever(settings.observeParticlesScroll()).thenReturn(particlesScrollSource)

        // When
        underTest.onStart()
        underTest.onStop()
        particlesScrollSource.onNext(true)

        // Then
        verify(view, never()).setChecked(any())
    }

    @Test
    fun storesParticlesScrollOnChange() {
        // Given
        val scroll = true

        // When
        underTest.onPreferenceChange(scroll)

        // Then
        verify(settings).particlesScroll = scroll
    }
}
