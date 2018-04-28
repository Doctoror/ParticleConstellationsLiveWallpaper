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
package com.doctoror.particleswallpaper.presentation.presenter

import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.interactor.ResetToDefaultsUseCase
import com.doctoror.particleswallpaper.presentation.view.ResetToDefaultsPreferenceView
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Test

class ResetToDefaultsPreferencePresenterTest {

    private val useCase: ResetToDefaultsUseCase = mock()
    private val view: ResetToDefaultsPreferenceView = mock()

    private val underTest = ResetToDefaultsPreferencePresenter(TrampolineSchedulers(), useCase)
            .apply { onTakeView(view) }

    @Test
    fun showsWarningDialogOnClick() {
        // When
        underTest.onClick()

        // Then
        verify(view).showWarningDialog()
    }

    @Test
    fun startsUseCaseOnResetClick() {
        // Given
        whenever(useCase.useCase()).thenReturn(Single.just(Unit))

        // When
        underTest.onResetClick()

        // Then
        verify(useCase).useCase()
    }
}
