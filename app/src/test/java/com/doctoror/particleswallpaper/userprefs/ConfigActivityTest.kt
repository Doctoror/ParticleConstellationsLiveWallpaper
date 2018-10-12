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
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import com.doctoror.particleswallpaper.R
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class ConfigActivityTest : KoinTest {

    private val menuPresenter: ConfigActivityMenuPresenter by inject()

    private val presenter: ConfigActivityPresenter by inject()

    private val underTest = ConfigActivity()

    private val underTestController = ActivityController.of(underTest)

    @Before
    fun setup() {
        declareMock<ConfigActivityMenuPresenter>()
        declareMock<ConfigActivityPresenter>()
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
    fun deliversOnActivityResult() {
        // Given
        underTestController.create()
        val requestCode = 1
        val resultCode = 2

        val onActivityResultMethod = underTest.javaClass.getDeclaredMethod(
            "onActivityResult",
            Int::class.java,
            Int::class.java,
            Intent::class.java
        ).apply {
            isAccessible = true
        }

        // When
        onActivityResultMethod.invoke(underTest, requestCode, resultCode, null)

        // Then
        verify(presenter).onActivityResult(requestCode, resultCode)
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
}
