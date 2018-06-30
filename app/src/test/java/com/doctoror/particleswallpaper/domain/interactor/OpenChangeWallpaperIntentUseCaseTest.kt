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
package com.doctoror.particleswallpaper.domain.interactor

import android.content.ActivityNotFoundException
import android.content.Intent
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_CHANGE_WALLPAPER
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.Test

class OpenChangeWallpaperIntentUseCaseTest {

    private val intentProvider: OpenChangeWallpaperIntentProvider = mock()
    private val action: StartActivityForResultAction = mock()

    private val underTest = OpenChangeWallpaperIntentUseCase(
            intentProvider, action)

    @Test
    fun opensWallpaperIntentWhenNotNull() {
        // Given
        val intent: Intent = mock()
        whenever(intentProvider.provideActionIntent()).thenReturn(intent)

        // When
        val o = underTest.useCase().test()

        // Then
        o.assertNoErrors()
        verify(action).startActivityForResult(intent, REQUEST_CODE_CHANGE_WALLPAPER)
    }

    @Test
    fun throwsWhenIntentIsNull() {
        // When
        val o = underTest.useCase().test()

        // Then
        o.assertError { it is RuntimeException }
        verify(action, never()).startActivityForResult(any(), any())
    }

    @Test
    fun deliversOnErrorOnActivityNotFoundException() {
        // Given
        val intent: Intent = mock()
        whenever(intentProvider.provideActionIntent()).thenReturn(intent)
        whenever(action.startActivityForResult(any(), any())).thenThrow(ActivityNotFoundException())

        // When
        val o = underTest.useCase().test()

        // Then
        o.assertError { it is ActivityNotFoundException }
    }
}
