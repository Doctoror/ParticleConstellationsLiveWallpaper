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
package com.doctoror.particleswallpaper.userprefs.multisampling

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.jupiter.api.Test

class MultisamplingPreferencePresenterTest {

    private val settings: OpenGlSettings = mock {
        on { it.observeNumSamples() }.doReturn(Observable.just(0))
    }

    private val settingsDevice: DeviceSettings = mock {
        on(it.observeMultisamplingSupportedValues()).doReturn(Observable.just(setOf("0")))
        on(it.observeOpenglEnabled()).doReturn(Observable.just(false))
    }

    private val wallpaperChecker: WallpaperCheckerUseCase = mock {
        on { it.wallpaperInstalledSource() }.doReturn(Single.just(false))
    }

    private val valueMapper: MultisamplingPreferenceValueMapper = mock {
        on { it.toEntries(any()) }.thenAnswer { invocation ->
            (invocation.getArgument(0) as Set<String>).toTypedArray()
        }

        on { it.toEntryValues(any()) }.thenAnswer { invocation ->
            (invocation.getArgument(0) as Set<String>).toTypedArray()
        }
    }

    private val view: MultisamplingPreferenceView = mock()

    private val underTest = MultisamplingPreferencePresenter(
        TrampolineSchedulers(), settings, settingsDevice, valueMapper, view, wallpaperChecker
    )

    @Test
    fun loadsNumSamplesAndSetsToView() {
        // Given
        val value = 4
        whenever(settings.observeNumSamples()).thenReturn(Observable.just(value))

        // When
        underTest.onStart()

        // Then
        verify(view).setValue(value)
    }

    @Test
    fun loadsUnsupportedStateAndSetsToView() {
        // Given
        whenever(settingsDevice.observeMultisamplingSupportedValues())
            .thenReturn(Observable.just(setOf("0")))

        // When
        underTest.onStart()

        // Then
        verify(view).setPreferenceSupported(false)
    }

    @Test
    fun loadsSupportedStateAndSetsToView() {
        // Given
        whenever(settingsDevice.observeOpenglEnabled())
            .thenReturn(Observable.just(true))

        whenever(settingsDevice.observeMultisamplingSupportedValues())
            .thenReturn(Observable.just(setOf("2", "3")))

        // When
        underTest.onStart()

        // Then
        verify(view).setPreferenceSupported(true)
    }

    @Test
    fun setsNumSamplesOnPreferenceChange() {
        // Given
        val value = 2

        // When
        underTest.onPreferenceChange(value)

        // Then
        verify(settings).numSamples = value
    }

    @Test
    fun loadsSupportedEntries() {
        // Given
        val expectedEntries = arrayOf<CharSequence>("4", "2")
        val supportedValues = setOf("4", "2")

        whenever(settingsDevice.observeMultisamplingSupportedValues())
            .thenReturn(Observable.just(supportedValues))

        // When
        underTest.onStart()

        // Then
        verify(view).setEntries(expectedEntries)
    }

    @Test
    fun loadsSupportedEntryValues() {
        // Given
        val expectedEntryValues = arrayOf<CharSequence>("2")
        val supportedValues = setOf("2")

        whenever(settingsDevice.observeMultisamplingSupportedValues())
            .thenReturn(Observable.just(supportedValues))

        // When
        underTest.onStart()

        // Then
        verify(view).setEntryValues(expectedEntryValues)
    }

    @Test
    fun marksUnsupportedWhenOpenGlDisabled() {
        // Given
        whenever(settingsDevice.observeOpenglEnabled())
            .thenReturn(Observable.just(false))

        whenever(settingsDevice.observeMultisamplingSupportedValues())
            .thenReturn(Observable.just(setOf("0", "2")))

        // When
        underTest.onStart()

        // Then
        verify(view).setPreferenceSupported(false)
    }

    @Test
    fun makrksSupportedWhenOpenGlEnabledAndHasSupportedValues() {
        // Given
        whenever(settingsDevice.observeMultisamplingSupportedValues())
            .thenReturn(Observable.just(setOf("0", "2")))

        whenever(settingsDevice.observeOpenglEnabled())
            .thenReturn(Observable.just(true))

        // When
        underTest.onStart()

        // Then
        verify(view).setPreferenceSupported(true)
    }

    @Test
    fun doesNotShowWallpaperRestartDialogIfNotSetYet() {
        // Given
        whenever(wallpaperChecker.wallpaperInstalledSource()).thenReturn(Single.just(false))

        // When
        underTest.onPreferenceChange(0)

        // Then
        verify(view, never()).showRestartDialog()
    }

    @Test
    fun showsWallpaperRestartDialogIfAlreadySet() {
        // Given
        whenever(wallpaperChecker.wallpaperInstalledSource()).thenReturn(Single.just(true))

        // When
        underTest.onPreferenceChange(0)

        // Then
        verify(view).showRestartDialog()
    }
}
