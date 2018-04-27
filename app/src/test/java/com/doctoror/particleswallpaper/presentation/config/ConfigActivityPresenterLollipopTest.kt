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
package com.doctoror.particleswallpaper.presentation.config

import android.app.ActionBar
import android.app.Activity
import android.view.*
import android.widget.Toolbar
import com.bumptech.glide.RequestManager
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.interactor.OpenChangeWallpaperIntentUseCase
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.functions.Consumer
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ConfigActivityPresenterLollipopTest {

    private val activity: Activity = mock()
    private val configurator: SceneConfigurator = mock()
    private val openChangeWallpaperIntentUseCase: OpenChangeWallpaperIntentUseCase = mock()
    private val settings: SettingsRepository = mock()
    private val requestManager: RequestManager = mock()
    private val view: ConfigActivityView = mock()

    private val underTest = ConfigActivityPresenterLollipop(
            activity,
            TrampolineSchedulers(),
            configurator,
            openChangeWallpaperIntentUseCase,
            requestManager,
            settings,
            view)

    @Test
    fun initsToolbarOnCreate() {
        // Given
        val actionBar: ActionBar = mock()
        whenever(activity.actionBar).thenReturn(actionBar)

        val toolbarContainer: ViewGroup = mock()
        whenever(activity.findViewById<ViewGroup>(R.id.toolbarContainer))
                .thenReturn(toolbarContainer)

        val toolbar: Toolbar = mock()
        val layoutInflater: LayoutInflater = mock {
            on(it.inflate(R.layout.activity_config_toolbar, toolbarContainer, false))
                    .doReturn(toolbar)
        }

        whenever(activity.layoutInflater).thenReturn(layoutInflater)

        // When
        underTest.onCreate()

        // Then
        verify(toolbarContainer).addView(toolbar, 0)
        verify(activity).setActionBar(toolbar)
        verify(actionBar).displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_HOME
    }

    @Test
    fun inflatesOptionsMenu() {
        // Given
        val menu: Menu = mock()
        val menuInflater: MenuInflater = mock()
        whenever(activity.menuInflater).thenReturn(menuInflater)

        // When
        val result = underTest.onCreateOptionsMenu(menu)

        // Then
        assertTrue(result)
        verify(menuInflater).inflate(R.menu.activity_config, menu)
    }

    @Test
    fun finishesOnUpButton() {
        // When
        underTest.onOptionsItemSelected(mockMenuItemWithId(android.R.id.home))

        // Then
        verify(activity).finish()
    }

    @Test
    fun subscribesToChangeWallpaperUseCaseOnPreviewClick() {
        // Given
        val useCaseSingle = spy(Single.just(true))
        whenever(openChangeWallpaperIntentUseCase.useCase())
                .thenReturn(useCaseSingle)

        // When
        underTest.onOptionsItemSelected(mockMenuItemWithId(R.id.actionPreview))

        // Then
        verify(openChangeWallpaperIntentUseCase).useCase()
        verify(useCaseSingle).subscribe(any<Consumer<Boolean>>())
    }

    @Test
    fun showsWallpaperStartFailureWhenFailed() {
        // Given
        whenever(openChangeWallpaperIntentUseCase.useCase())
                .thenReturn(Single.just(false))

        // When
        underTest.onOptionsItemSelected(mockMenuItemWithId(R.id.actionPreview))

        // Then
        verify(view).showWallpaperPreviewStartFailed()
    }

    private fun mockMenuItemWithId(id: Int): MenuItem = mock {
        whenever(it.itemId).doReturn(id)
    }
}
