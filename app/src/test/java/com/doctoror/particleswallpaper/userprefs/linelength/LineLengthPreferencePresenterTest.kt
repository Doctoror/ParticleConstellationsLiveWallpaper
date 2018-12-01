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
package com.doctoror.particleswallpaper.userprefs.linelength

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.preference.SeekBarPreferenceView
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class LineLengthPreferencePresenterTest {

    private val settings: SceneSettings = mock()
    private val view: SeekBarPreferenceView = mock()

    private val underTest = LineLengthPreferencePresenter(TrampolineSchedulers(), settings, view)

    @Test
    fun testMapper() {
        com.doctoror.particleswallpaper.framework.preference.testMapper(underTest)
    }

    @Test
    fun setsMaxValueOnInit() {
        verify(view).setMaxInt(100)
    }

    @Test
    fun setsLineLengthOnStart() {
        // Given
        val length = 12f
        whenever(settings.observeLineLength()).thenReturn(Observable.just(length))

        // When
        underTest.onStart()

        // Then
        verify(view).setProgressInt((length / 3f).toInt())
    }

    @Test
    fun doesNotSetLineLengthAfterOnStop() {
        // Given
        val lineLengthSource = PublishSubject.create<Float>()
        whenever(settings.observeLineLength()).thenReturn(lineLengthSource)

        // When
        underTest.onStart()
        underTest.onStop()
        lineLengthSource.onNext(1f)

        // Then
        verify(view, never()).setProgressInt(any())
    }

    @Test
    fun storesLineLengthOnChange() {
        // Given
        val progress = 1

        // When
        underTest.onPreferenceChange(progress)

        // Then
        verify(settings).lineLength = progress.toFloat() * 3f
    }
}
