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
package com.doctoror.particleswallpaper.userprefs.particlesscroll

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ParticlesScrollPreferenceTest : KoinTest {

    private val presenter: ParticlesScrollPreferencePresenter by inject()

    private val underTest =
        ParticlesScrollPreference(ApplicationProvider.getApplicationContext<Context>())

    @Before
    fun setup() {
        declareMock<ParticlesScrollPreferencePresenter>()
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
}
