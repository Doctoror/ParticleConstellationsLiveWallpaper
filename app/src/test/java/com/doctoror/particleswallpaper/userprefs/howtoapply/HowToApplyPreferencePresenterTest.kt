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
package com.doctoror.particleswallpaper.userprefs.howtoapply

import android.content.Intent
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HowToApplyPreferencePresenterTest {

    private val intentProvider: OpenChangeWallpaperIntentProvider = mock()

    private val view: HowToApplyPreferenceView = mock()

    private val underTest = HowToApplyPreferencePresenter(intentProvider, view)

    @Test
    fun showsDialogHowToApplyUsingPreviewWhenPreviewSupported() {
        // Given
        val intent: Intent = mock()
        whenever(intentProvider.provideActionIntent()).thenReturn(intent)

        // When
        underTest.onClick()

        // Then
        verify(view).showDialogHowToApplyUsingPreview()
    }

    @Test
    fun showsDialogHowToApplyUsingChooserWhenChooserSupported() {
        // Given
        val intent: Intent = mock()
        whenever(intentProvider.provideActionIntent()).thenReturn(intent)
        whenever(intentProvider.isWallaperChooserAction(intent)).thenReturn(true)

        // When
        underTest.onClick()

        // Then
        verify(view).showDialogHowToApplyUsingChooser()
    }

    @Test
    fun showsDialogHowToApplyManuallyWhenNoIntentSupported() {
        // When
        underTest.onClick()

        // Then
        verify(view).showDialogHowToApplyWithoutPreview()
    }
}
