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
import io.reactivex.Observable
import io.reactivex.subjects.AsyncInitialValueBehaviorSubject

const val PREFERENCES_NAME_PRIVACY = "privacy"
const val KEY_CRASH_REPORTING_ENABLED = "crash_reporting_enabled"

class PrivacySettings(private val prefsSource: () -> SharedPreferences) {

    private val crashReportingEnabledSubject =
        AsyncInitialValueBehaviorSubject { crashReportingEnabled }.toSerialized()

    fun observeCrashReportingEnabled(): Observable<CrashReportingEnabledState> =
        crashReportingEnabledSubject

    var crashReportingEnabled: CrashReportingEnabledState
        get() = CrashReportingEnabledState.valueOf(
            prefsSource().getString(
                KEY_CRASH_REPORTING_ENABLED,
                CrashReportingEnabledState.UNRESOVLED.name
            ) ?: CrashReportingEnabledState.UNRESOVLED.name
        )
        set(value) {
            prefsSource().edit().putString(KEY_CRASH_REPORTING_ENABLED, value.name).apply()
            crashReportingEnabledSubject.onNext(crashReportingEnabled)
        }
}
