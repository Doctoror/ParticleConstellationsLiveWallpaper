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
package com.doctoror.particleswallpaper.presentation.preference

import com.doctoror.particleswallpaper.presentation.presenter.NumDotsPreferencePresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class NumDotsPreferenceTest {

    private val presenter: NumDotsPreferencePresenter = mock()
    private val underTest = NumDotsPreference(RuntimeEnvironment.application).apply {
        this.presenter = this@NumDotsPreferenceTest.presenter
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

    @Test
    fun setsSummary() {
        // Given
        val progress = 3

        // When
        underTest.setProgressInt(progress)

        // Then
        assertEquals(underTest.summary, (progress + 1).toString())
    }
}
