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

import android.app.Activity
import android.app.Fragment
import android.content.res.TypedArray
import com.doctoror.particleswallpaper.presentation.presenter.PreviewPreferencePresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class PreviewPreferenceTest {

    private val mockTypedArray: TypedArray = mock()

    private val activity: Activity = mock {
        on(it.obtainStyledAttributes(anyOrNull(), any(), any(), any())).doReturn(mockTypedArray)
        on(it.packageName).doReturn("com.doctoror.particleswallpaper")
    }

    private val presenter: PreviewPreferencePresenter = mock()

    private val underTest = PreviewPreference(activity).apply {
        val presenterField = PreviewPreference::class.java.getDeclaredField("presenter").apply {
            isAccessible = true
        }
        presenterField.set(this, presenter)
    }

    @Test
    fun deliversHostToPresenter() {
        // Given
        val host: Fragment = mock()

        // When
        underTest.host = host

        // Then
        verify(presenter).host = host
    }

    @Test
    fun deliversUseCaseWithHost() {
        // When
        underTest.host = mock()

        // Then
        verify(presenter).useCase = any()
    }

    @Test
    fun resetsUseCaseWithHost() {
        // When
        underTest.host = null

        // Then
        verify(presenter).useCase = null
    }
}
