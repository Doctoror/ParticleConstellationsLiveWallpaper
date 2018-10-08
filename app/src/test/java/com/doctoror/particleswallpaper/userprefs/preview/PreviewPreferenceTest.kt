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
package com.doctoror.particleswallpaper.userprefs.preview

import android.app.Activity
import android.app.Fragment
import android.content.res.TypedArray
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class PreviewPreferenceTest : KoinTest {

    private val mockTypedArray: TypedArray = mock()

    private val activity: Activity = mock {
        on(it.obtainStyledAttributes(anyOrNull(), any(), any(), any())).doReturn(mockTypedArray)
        on(it.packageName).doReturn("com.doctoror.particleswallpaper")
    }

    private val presenter: PreviewPreferencePresenter by inject()

    private val underTest = PreviewPreference(activity)

    @Before
    fun setup() {
        declareMock<PreviewPreferencePresenter>()
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun deliversHostToPresenter() {
        // Given
        val host: Fragment = mock()

        // When
        underTest.fragment = host

        // Then
        verify(presenter).host = host
    }

    @Test
    fun deliversUseCaseWithHost() {
        // When
        underTest.fragment = mock()

        // Then
        verify(presenter).useCase = any()
    }

    @Test
    fun resetsUseCaseWithHost() {
        // When
        underTest.fragment = null

        // Then
        verify(presenter).useCase = null
    }
}
