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
package com.doctoror.particleswallpaper.userprefs

import android.view.Menu
import android.view.MenuItem
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentUseCase
import io.reactivex.Completable
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ConfigActivityMenuPresenterTest {

    private val openChangeWallpaperIntentProvider: OpenChangeWallpaperIntentProvider = mock()
    private val openChangeWallpaperIntentUseCase: OpenChangeWallpaperIntentUseCase = mock()
    private val view: ConfigActivityMenuView = mock()

    private val underTest = ConfigActivityMenuPresenter(
        openChangeWallpaperIntentProvider,
        openChangeWallpaperIntentUseCase,
        view
    )

    @Test
    fun inflatesOptionsMenuWhenHasWallpaperIntent() {
        // Given
        val menu: Menu = mock()
        whenever(openChangeWallpaperIntentProvider.provideActionIntent()).thenReturn(mock())

        // When
        val result = underTest.onCreateOptionsMenu(menu)

        // Then
        assertTrue(result)
        verify(view).inflateMenu(menu)
    }

    @Test
    fun doesNotInflateOptionsMenuWhenHasNoWallpaperIntent() {
        // When
        val result = underTest.onCreateOptionsMenu(mock())

        // Then
        assertTrue(result)
        verify(view, never()).inflateMenu(any())
    }

    @Test
    fun finishesOnUpButton() {
        // When
        underTest.onOptionsItemSelected(mockMenuItemWithId(android.R.id.home))

        // Then
        verify(view).finish()
    }

    @Test
    fun subscribesToChangeWallpaperUseCaseOnPreviewClick() {
        // Given
        val useCaseCompletable = spy(Completable.complete())
        whenever(openChangeWallpaperIntentUseCase.action())
            .thenReturn(useCaseCompletable)

        // When
        underTest.onOptionsItemSelected(mockMenuItemWithId(R.id.actionApply))

        // Then
        verify(openChangeWallpaperIntentUseCase).action()
        verify(useCaseCompletable).subscribe()
    }

    private fun mockMenuItemWithId(id: Int): MenuItem = mock {
        whenever(it.itemId).doReturn(id)
    }
}
