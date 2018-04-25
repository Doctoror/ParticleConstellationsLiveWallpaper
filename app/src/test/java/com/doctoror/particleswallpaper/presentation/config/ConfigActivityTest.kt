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

import android.view.Menu
import android.view.MenuItem
import com.doctoror.particleswallpaper.presentation.di.Injector
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ConfigActivityTest {

    private val presenter: ConfigActivityPresenter = mock()

    private val underTest = ConfigActivity()

    private val underTestController = ActivityController.of(underTest)

    @Before
    fun test() {
        Injector.getInstance(underTest).configComponent
    }

    @Test
    fun deliversOnCreateOptionsMenu() {
        // Given
        underTestController.create()
        replacePresenter()
        val menu: Menu = mock()

        // When
        underTest.onCreateOptionsMenu(menu)

        // Then
        verify(presenter).onCreateOptionsMenu(menu)
    }

    @Test
    fun deliversOnOptionsItemSelected() {
        // Given
        underTestController.create()
        replacePresenter()
        val menuItem: MenuItem = mock()

        // When
        underTest.onOptionsItemSelected(menuItem)

        // Then
        verify(presenter).onOptionsItemSelected(menuItem)
    }

    @Test
    fun returnsActivity() {
        // When
        val result = underTest.getActivity()

        // Then
        assertEquals(underTest, result)
    }

    private fun replacePresenter() {
        underTest.presenter = this@ConfigActivityTest.presenter
    }
}
