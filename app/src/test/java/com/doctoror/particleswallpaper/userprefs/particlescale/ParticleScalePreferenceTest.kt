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
package com.doctoror.particleswallpaper.userprefs.particlescale

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class ParticleScalePreferenceTest : KoinTest {

    private val presenter: ParticleScalePreferencePresenter by inject()
    private val underTest =
        ParticleScalePreference(ApplicationProvider.getApplicationContext<Context>()).apply {
            setMaxInt(70)
        }

    @Before
    fun setup() {
        stopKoin()
        startKoin {
            modules(
                module {
                    single { mock<ParticleScalePreferencePresenter>() }
                }
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun deliversOnStartToPresenter() {
        // When
        underTest.onStart(mock())

        // Then
        verify(presenter).onStart()
    }

    @Test
    fun deliversOnStopToPresenter() {
        // When
        underTest.onStop(mock())

        // Then
        verify(presenter).onStop()
    }

    @Test
    fun retunsSetMaxInt() {
        // Given
        val maxInt = 10

        // When
        underTest.setMaxInt(maxInt)

        // Then
        assertEquals(maxInt, underTest.getMaxInt())
    }

    @Test
    fun setsProgressInt() {
        // Given
        val progress = 5

        // When
        underTest.setProgressInt(progress)

        // Then
        assertEquals(underTest.progress, progress)
    }
}
