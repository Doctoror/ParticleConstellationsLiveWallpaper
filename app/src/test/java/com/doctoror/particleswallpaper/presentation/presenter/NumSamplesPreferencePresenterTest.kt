package com.doctoror.particleswallpaper.presentation.presenter

import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.interactor.WallpaperCheckerUseCase
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.presentation.view.NumSamplesPreferenceView
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test

class NumSamplesPreferencePresenterTest {

    private val settings: MutableSettingsRepository = mock {
        on { it.getNumSamples() }.doReturn(Observable.just(0))
    }

    private val wallpaperChecker: WallpaperCheckerUseCase = mock {
        on { it.useCase() }.doReturn(Single.just(false))
    }

    private val view: NumSamplesPreferenceView = mock()

    private val underTest = NumSamplesPreferencePresenter(
            TrampolineSchedulers(), settings, wallpaperChecker).apply {
        onTakeView(view)
    }

    @Test
    fun loadsNumSamplesAndSetsToView() {
        // Given
        val value = 4
        whenever(settings.getNumSamples()).thenReturn(Observable.just(value))

        // When
        underTest.onStart()

        // Then
        verify(view).setValue(value)
    }

    @Test
    fun setsNumSamplesOnPreferenceChange() {
        // Given
        val value = 2

        // When
        underTest.onPreferenceChange(value)

        // Then
        verify(settings).setNumSamples(value)
    }

    @Test
    fun doesNotShowWallpaperRestartDialogIfNotSetYet() {
        // Given
        whenever(wallpaperChecker.useCase()).thenReturn(Single.just(false))

        // When
        underTest.onPreferenceChange(0)

        // Then
        verify(view, never()).showRestartDialog()
    }

    @Test
    fun showsWallpaperRestartDialogIfAlreadySet() {
        // Given
        whenever(wallpaperChecker.useCase()).thenReturn(Single.just(true))

        // When
        underTest.onPreferenceChange(0)

        // Then
        verify(view).showRestartDialog()
    }
}
