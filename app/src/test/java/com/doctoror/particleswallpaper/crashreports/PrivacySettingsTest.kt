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
package com.doctoror.particleswallpaper.crashreports

import android.content.SharedPreferences
import com.doctoror.particleswallpaper.userprefs.data.InMemorySharedPreferences
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PrivacySettingsTest {

    private val prefs: SharedPreferences = mock()

    private val underTest = PrivacySettings { prefs }

    @Test
    fun observesCrashReportingEnabledValueChanges() {
        val prefs = InMemorySharedPreferences()
        val underTest = PrivacySettings { prefs }

        val expectedValue1 = CrashReportingEnabledState.UNRESOVLED
        val expectedValue2 = CrashReportingEnabledState.ENABLED
        val expectedValue3 = CrashReportingEnabledState.DISABLED

        val o = underTest.observeCrashReportingEnabled().test()

        underTest.crashReportingEnabled = expectedValue2
        underTest.crashReportingEnabled = expectedValue3

        o.assertValues(expectedValue1, expectedValue2, expectedValue3)
    }

    @Test
    fun returnsCrashReportingEnabledValueFromPrefs() {
        val expectedValue = CrashReportingEnabledState.ENABLED

        whenever(prefs.getString(eq(KEY_CRASH_REPORTING_ENABLED), any()))
            .thenReturn(expectedValue.name)

        assertEquals(expectedValue, underTest.crashReportingEnabled)
    }

    @Test
    fun storesCrashReportingEnabledValueInPrefs() {
        val editor: SharedPreferences.Editor = mock()
        whenever(editor.putString(any(), any())).thenReturn(editor)
        whenever(prefs.edit()).thenReturn(editor)

        val value = CrashReportingEnabledState.DISABLED

        underTest.crashReportingEnabled = value

        verify(editor).putString(KEY_CRASH_REPORTING_ENABLED, value.name)
        verify(editor).apply()
    }
}
