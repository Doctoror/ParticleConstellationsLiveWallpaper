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
package com.doctoror.particleswallpaper.userprefs.speedfactor

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SpeedFactorPreferenceTest : KoinTest {

    private val presenter: SpeedFactorPreferencePresenter by inject()

    private val underTest =
        SpeedFactorPreference(ApplicationProvider.getApplicationContext<Context>()).apply {
            setMaxInt(40)
        }

    @Before
    fun setup() {
        declareMock<SpeedFactorPreferencePresenter>()
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun deliversOnStartToPresenter() {
        // When
        underTest.onStart()

        // Then
        verify(presenter).onStart()
    }

    @Test
    fun deliversOnStopToPresenter() {
        // When
        underTest.onStop()

        // Then
        verify(presenter).onStop()
    }

    @Test
    fun retunsSetMaxInt() {
        // Given
        val maxInt = 2

        // When
        underTest.setMaxInt(maxInt)

        // Then
        assertEquals(maxInt, underTest.getMaxInt())
    }

    @Test
    fun setsProgressInt() {
        // Given
        val progress = 3

        // When
        underTest.setProgressInt(progress)

        // Then
        assertEquals(underTest.progress, progress)
    }
}
