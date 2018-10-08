package com.doctoror.particleswallpaper.userprefs.multisampling

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test

class MultisamplingPreferencePresenterTest {

    private val settings: OpenGlSettings = mock {
        on { it.observeNumSamples() }.doReturn(Observable.just(0))
    }

    private val settingsDevice: DeviceSettings = mock {
        on { it.observeMultisamplingSupported() }.doReturn(Observable.just(false))
    }

    private val wallpaperChecker: WallpaperCheckerUseCase = mock {
        on { it.wallpaperInstalledSource() }.doReturn(Single.just(false))
    }

    private val view: MultisamplingPreferenceView = mock()

    private val underTest = MultisamplingPreferencePresenter(
        TrampolineSchedulers(), settings, settingsDevice, view, wallpaperChecker
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
    fun loadsSupportedStateAndSetsToView() {
        // Given
        val value = true
        whenever(settingsDevice.observeMultisamplingSupported()).thenReturn(Observable.just(value))

        // When
        underTest.onStart()

        // Then
        verify(view).setPreferenceSupported(value)
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
