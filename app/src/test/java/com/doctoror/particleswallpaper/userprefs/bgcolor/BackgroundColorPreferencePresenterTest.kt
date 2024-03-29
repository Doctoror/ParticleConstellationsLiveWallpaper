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
package com.doctoror.particleswallpaper.userprefs.bgcolor

import android.graphics.Color
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.userprefs.data.DefaultSceneSettings
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.kotlin.*

class BackgroundColorPreferencePresenterTest {

    private val settings: SceneSettings = mock()
    private val defaults: DefaultSceneSettings = mock()
    private val view: BackgroundColorPreferenceView = mock()

    private val underTest = BackgroundColorPreferencePresenter(
        TrampolineSchedulers(), settings, defaults, view
    )

    @Test
    fun setsColorOnStart() {
        // Given
        val color = Color.CYAN
        whenever(settings.observeBackgroundColor()).thenReturn(Observable.just(color))

        // When
        underTest.onStart()

        // Then
        verify(view).setColor(color)
    }

    @Test
    fun doesNotSetColorAfterOnStop() {
        // Given
        val colorSource = PublishSubject.create<Int>()
        whenever(settings.observeBackgroundColor()).thenReturn(colorSource)

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
        verify(settings).backgroundColor = color
    }

    @Test
    fun storesDefaultColorOnNull() {
        // Given
        val defaultColor = Color.DKGRAY
        whenever(defaults.backgroundColor).thenReturn(defaultColor)

        // When
        underTest.onPreferenceChange(null)

        // Then
        verify(settings).backgroundColor = defaultColor
    }
}
