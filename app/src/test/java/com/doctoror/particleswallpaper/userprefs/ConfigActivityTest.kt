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

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.userprefs.bgimage.BackgroundImagePreferencePresenter
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import io.reactivex.Observable
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class ConfigActivityTest : KoinTest {

    private val particlesViewGenerator: ParticlesViewGenerator by inject()

    private val menuPresenter: ConfigActivityMenuPresenter by inject()

    private val underTest = ConfigActivity()

    private val underTestController = ActivityController.of(underTest)

    @Before
    fun setup() {
        declareMock<BackgroundImagePreferencePresenter>()
        declareMock<ConfigActivityMenuPresenter>()
        declareMock<OpenChangeWallpaperIntentProvider>()
        declareMock<ParticlesViewGenerator>()
        declareMock<SceneBackgroundView>()

        whenever(particlesViewGenerator.observeParticlesViewInstance())
            .thenReturn(Observable.empty())
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun deliversOnCreateOptionsMenu() {
        // Given
        underTestController.create()
        val menu: Menu = mock()

        // When
        underTest.onCreateOptionsMenu(menu)

        // Then
        verify(menuPresenter).onCreateOptionsMenu(menu)
    }

    @Test
    fun deliversOnOptionsItemSelected() {
        // Given
        underTestController.create()
        val menuItem: MenuItem = mock()

        // When
        underTest.onOptionsItemSelected(menuItem)

        // Then
        verify(menuPresenter).onOptionsItemSelected(menuItem)
    }

    @Test
    fun finishesWhenWallpaperSet() {
        // Given
        underTestController.create()
        val requestCode = REQUEST_CODE_CHANGE_WALLPAPER
        val resultCode = Activity.RESULT_OK

        // When
        invokeOnActivityResult(requestCode, resultCode)

        // Then
        assertTrue(underTest.isFinishing)
    }

    @Test
    fun doesNotFinisheWhenWallpaperSetCanceled() {
        // Given
        underTestController.create()
        val requestCode = REQUEST_CODE_CHANGE_WALLPAPER
        val resultCode = Activity.RESULT_CANCELED

        // When
        invokeOnActivityResult(requestCode, resultCode)

        // Then
        assertFalse(underTest.isFinishing)
    }

    @Test
    fun doesNotFinisheOnArbitraryResult() {
        // Given
        underTestController.create()
        val requestCode = 0
        val resultCode = Activity.RESULT_OK

        // When
        invokeOnActivityResult(requestCode, resultCode)

        // Then
        assertFalse(underTest.isFinishing)
    }

    @Test
    fun noToolbarUntilSetupCalled() {
        // When
        underTestController.create()

        // Then
        assertNull(findToolbar())
    }

    @Test
    fun hasToolbarWhenSetupToolbarCalled() {
        // When
        underTestController.create()
        underTest.setupToolbar()

        // Then
        assertNotNull(findToolbar())
    }

    @Test
    fun hasActionBarOptionsWhenSetupToolbarCalled() {
        // When
        underTestController.create()
        underTest.setupToolbar()

        // Then
        val expectedDisplayOptions = ActionBar.DISPLAY_HOME_AS_UP or
                ActionBar.DISPLAY_SHOW_HOME

        assertEquals(expectedDisplayOptions, underTest.actionBar!!.displayOptions)
    }

    private fun findToolbar(): View? {
        val toolbarContainer = underTest.findViewById<ViewGroup>(R.id.toolbarContainer)!!
        return (0..toolbarContainer.childCount)
            .map { toolbarContainer.getChildAt(it) }
            .find { it is Toolbar }
    }

    private fun invokeOnActivityResult(requestCode: Int, resultCode: Int) {
        val onActivityResultMethod = underTest.javaClass.getDeclaredMethod(
            "onActivityResult",
            Int::class.java,
            Int::class.java,
            Intent::class.java
        ).apply {
            isAccessible = true
        }

        onActivityResultMethod.invoke(underTest, requestCode, resultCode, null)
    }
}
