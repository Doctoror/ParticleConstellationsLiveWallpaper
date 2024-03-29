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
package com.doctoror.particleswallpaper.userprefs.resettodefaults

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import io.reactivex.Completable
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ResetToDefaultsPreferencePresenterTest {

    private val useCase: ResetToDefaultsUseCase = mock()
    private val view: ResetToDefaultsPreferenceView = mock()

    private val underTest = ResetToDefaultsPreferencePresenter(
        TrampolineSchedulers(), useCase, view
    )

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
        whenever(useCase.action()).thenReturn(Completable.complete())

        // When
        underTest.onResetClick()

        // Then
        verify(useCase).action()
    }
}
