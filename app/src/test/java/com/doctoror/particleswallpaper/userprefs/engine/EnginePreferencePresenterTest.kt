/*
 * Copyright (C) 2019 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.userprefs.engine

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.util.OpenGlEnabledStateChanger
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.doctoror.particleswallpaper.userprefs.multisampling.WallpaperCheckerUseCase
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test
import org.mockito.kotlin.*

private const val VALUE_OPENGL = "valueOpengl"
private const val VALUE_CANVAS = "valueCanvas"

class EnginePreferencePresenterTest {

    private val openGlEnabledStateChanger: OpenGlEnabledStateChanger = mock()

    private val settingsDevice: DeviceSettings = mock {
        on(it.observeMultisamplingSupportedValues()).doReturn(Observable.just(setOf("0")))
        on(it.observeOpenglEnabled()).doReturn(Observable.just(true))
        on(it.openglEnabled).thenReturn(true)
    }

    private val wallpaperChecker: WallpaperCheckerUseCase = mock {
        on { it.wallpaperInstalledSource() }.doReturn(Single.just(false))
    }

    private val valueMapper: EnginePreferenceValueMapper = mock {
        on(it.provideEntries()).thenReturn(arrayOf("entryOpengl", "entryCanvas"))
        on(it.provideEntryValues()).thenReturn(arrayOf(VALUE_OPENGL, VALUE_CANVAS))

        on(it.openglEnabledStateToValue(true)).thenReturn(VALUE_OPENGL)
        on(it.openglEnabledStateToValue(false)).thenReturn(VALUE_CANVAS)

        on(it.valueToOpenglEnabledState(VALUE_OPENGL)).thenReturn(true)
        on(it.valueToOpenglEnabledState(VALUE_CANVAS)).thenReturn(false)
    }

    private val view: EnginePreferenceView = mock()

    private val underTest = EnginePreferencePresenter(
        openGlEnabledStateChanger,
        TrampolineSchedulers(),
        settingsDevice,
        valueMapper,
        view,
        wallpaperChecker
    )

    @Test
    fun loadsValueAndSetsToView() {
        // Given
        val enabled = true
        whenever(settingsDevice.observeOpenglEnabled()).thenReturn(Observable.just(enabled))

        // When
        underTest.onStart()

        // Then
        verify(view).setValue(valueMapper.openglEnabledStateToValue(enabled))
    }

    @Test
    fun setsValueOnPreferenceChange() {
        // Given
        val value = VALUE_CANVAS

        // When
        underTest.onPreferenceChange(value)

        // Then
        verify(openGlEnabledStateChanger).setOpenglGlEnabled(
            openGlEnabled = valueMapper.valueToOpenglEnabledState(value),
            shouldKillApp = false
        )
        verify(settingsDevice, never()).openglEnabled = any()
    }

    @Test
    fun doesNothingWhenTheValueIsAlreadySet() {
        // Given
        val value = VALUE_OPENGL

        // When
        underTest.onPreferenceChange(value)

        // Then
        verify(openGlEnabledStateChanger, never()).setOpenglGlEnabled(
            openGlEnabled = any(),
            shouldKillApp = any()
        )
        verify(settingsDevice, never()).openglEnabled = any()
    }

    @Test
    fun doesNotShowWallpaperRestartDialogIfNotSetYet() {
        // Given
        whenever(wallpaperChecker.wallpaperInstalledSource()).thenReturn(Single.just(false))

        // When
        underTest.onPreferenceChange(VALUE_CANVAS)

        // Then
        verify(view, never()).showRestartDialog(any())
    }

    @Test
    fun showsWallpaperRestartDialogIfAlreadySet() {
        // Given
        whenever(wallpaperChecker.wallpaperInstalledSource()).thenReturn(Single.just(true))
        val value = VALUE_CANVAS

        // When
        underTest.onPreferenceChange(value)

        // Then
        verify(view).showRestartDialog(valueMapper.valueToOpenglEnabledState(value))
    }
}
