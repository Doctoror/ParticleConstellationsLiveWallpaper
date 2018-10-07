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
package com.doctoror.particleswallpaper.userprefs.data

import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeviceSettingsTest {

    private val prefs: SharedPreferences = mock()

    private val underTest = DeviceSettings(prefs)

    @Test
    fun observesMultisamplingSupportedChanges() {
        val prefs = InMemorySharedPreferences()
        val underTest = DeviceSettings(prefs)

        val o = underTest.observeMultisamplingSupported().test()

        underTest.multisamplingSupported = false
        underTest.multisamplingSupported = true

        o.assertValues(true, false, true)
    }

    @Test
    fun returnsMultisamplingSupportedValueFromPrefs() {
        whenever(prefs.getBoolean(KEY_MULTISAMPLING_SUPPORTED, true))
                .thenReturn(true)

        assertEquals(true, underTest.multisamplingSupported)
    }

    @Test
    fun storessMultisamplingSupportedValueInPrefs() {
        val editor: SharedPreferences.Editor = mock()
        whenever(editor.putBoolean(any(), any())).thenReturn(editor)
        whenever(prefs.edit()).thenReturn(editor)

        underTest.multisamplingSupported = true

        verify(editor).putBoolean(KEY_MULTISAMPLING_SUPPORTED, true)
        verify(editor).apply()
    }
}
