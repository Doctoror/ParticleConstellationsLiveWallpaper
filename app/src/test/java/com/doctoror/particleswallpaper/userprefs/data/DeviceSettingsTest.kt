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
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

class DeviceSettingsTest {

    private val prefs: SharedPreferences = mock()

    private val underTest = DeviceSettings { prefs }

    @Test
    fun observesMultisamplingSupportedValuesChanges() {
        val prefs = InMemorySharedPreferences()
        val underTest = DeviceSettings { prefs }

        val expectedValue1 = setOf("4", "2")
        val expectedValue2 = setOf("2")
        val expectedValue3 = emptySet<String>()

        val o = underTest.observeMultisamplingSupportedValues().test()

        underTest.multisamplingSupportedValues = expectedValue2
        underTest.multisamplingSupportedValues = expectedValue3

        o.assertValues(expectedValue1, expectedValue2, expectedValue3)
    }

    @Test
    fun returnsMultisamplingSupportedValuesFromPrefs() {
        val expectedValue = setOf("4", "2")

        whenever(prefs.getStringSet(eq(KEY_MULTISAMPLING_SUPPORTED_VALUES), any()))
            .thenReturn(expectedValue)

        assertEquals(expectedValue, underTest.multisamplingSupportedValues)
    }

    @Test
    fun storesMultisamplingSupportedValuesInPrefs() {
        val editor: SharedPreferences.Editor = mock()
        whenever(editor.putStringSet(any(), any())).thenReturn(editor)
        whenever(prefs.edit()).thenReturn(editor)

        val values = setOf("4", "2")

        underTest.multisamplingSupportedValues = values

        verify(editor).putStringSet(KEY_MULTISAMPLING_SUPPORTED_VALUES, values)
        verify(editor).apply()
    }

    @Test
    fun observesOpenglEnabledChanges() {
        val prefs = InMemorySharedPreferences()
        val underTest = DeviceSettings { prefs }

        val expectedValue1 = false
        val expectedValue2 = true
        val expectedValue3 = false

        val o = underTest.observeOpenglEnabled().test()

        underTest.openglEnabled = expectedValue2
        underTest.openglEnabled = expectedValue3

        o.assertValues(expectedValue1, expectedValue2, expectedValue3)
    }

    @Test
    fun returnsOpenglEnabledValuesFromPrefs() {
        val expectedValue = true

        whenever(prefs.getBoolean(eq(KEY_OPENGL_ENABLED), any()))
            .thenReturn(expectedValue)

        assertEquals(expectedValue, underTest.openglEnabled)
    }

    @Test
    fun storesOpenglEnabledValuesInPrefs() {
        val editor: SharedPreferences.Editor = mock()
        whenever(editor.putBoolean(any(), any())).thenReturn(editor)
        whenever(prefs.edit()).thenReturn(editor)

        val value = false

        underTest.openglEnabled = value

        verify(editor).putBoolean(KEY_OPENGL_ENABLED, value)
        verify(editor).commit()
    }
}
