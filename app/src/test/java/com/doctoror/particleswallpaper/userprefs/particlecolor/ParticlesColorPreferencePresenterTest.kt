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
package com.doctoror.particleswallpaper.userprefs.particlecolor

import android.graphics.Color
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.userprefs.data.DefaultSceneSettings
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class ParticlesColorPreferencePresenterTest {

    private val settings: SceneSettings = mock()
    private val defaults: DefaultSceneSettings = mock()
    private val view: ParticlesColorPreferenceView = mock()

    private val underTest = ParticlesColorPreferencePresenter(
        TrampolineSchedulers(), settings, defaults
    ).apply {
        onTakeView(view)
    }

    @Test
    fun setsColorOnStart() {
        // Given
        val color = Color.GRAY
        whenever(settings.observeParticleColor()).thenReturn(Observable.just(color))

        // When
        underTest.onStart()

        // Then
        verify(view).setColor(color)
    }

    @Test
    fun doesNotSetColorAfterOnStop() {
        // Given
        val colorSource = PublishSubject.create<Int>()
        whenever(settings.observeParticleColor()).thenReturn(colorSource)

        // When
        underTest.onStart()
        underTest.onStop()
        colorSource.onNext(Color.DKGRAY)

        // Then
        verify(view, never()).setColor(any())
    }

    @Test
    fun storesColorOnChange() {
        // Given
        val color = Color.BLACK

        // When
        underTest.onPreferenceChange(color)

        // Then
        verify(settings).particleColor = color
    }

    @Test
    fun storesDefaultColorOnNull() {
        // Given
        val defaultColor = Color.DKGRAY
        whenever(defaults.particleColor).thenReturn(defaultColor)

        // When
        underTest.onPreferenceChange(null)

        // Then
        verify(settings).particleColor = defaultColor
    }

    @Test
    fun showsPreferencesDialogOnClick() {
        // When
        underTest.onClick()

        // Then
        verify(view).showPreferenceDialog()
    }
}
