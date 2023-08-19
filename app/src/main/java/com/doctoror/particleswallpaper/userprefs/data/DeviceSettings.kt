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

import android.annotation.SuppressLint
import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.subjects.AsyncInitialValueBehaviorSubject

const val PREFERENCES_NAME_DEVICE = "prefs_device"
const val KEY_MULTISAMPLING_SUPPORTED_VALUES = "multisampling_supported_values"
const val KEY_OPENGL_ENABLED = "opengl_enabled3"
const val KEY_OPENGL_SUPPORTED = "opengl_enabled" // renamed, left value for backward compatibility

private val DEFAULT_MULTISAMPLING_SUPPORTED_VALUES = setOf("2", "4")

class DeviceSettings(private val prefsSource: () -> SharedPreferences) {

    private val multisamplingSupportedValuesSubject =
        AsyncInitialValueBehaviorSubject { multisamplingSupportedValues }.toSerialized()

    private val openglEnabledSubject =
        AsyncInitialValueBehaviorSubject { openglEnabled }.toSerialized()

    private val openglSupportedSubject =
        AsyncInitialValueBehaviorSubject { openglSupported }.toSerialized()

    fun observeMultisamplingSupportedValues(): Observable<Set<String>> =
        multisamplingSupportedValuesSubject

    fun observeOpenglEnabled(): Observable<Boolean> =
        openglEnabledSubject

    fun observeOpenglSupported(): Observable<Boolean> =
        openglSupportedSubject

    var multisamplingSupportedValues: Set<String>
        get() = prefsSource().getStringSet(
            KEY_MULTISAMPLING_SUPPORTED_VALUES,
            DEFAULT_MULTISAMPLING_SUPPORTED_VALUES
        )!!
        set(value) {
            prefsSource().edit().putStringSet(KEY_MULTISAMPLING_SUPPORTED_VALUES, value).apply()
            multisamplingSupportedValuesSubject.onNext(value)
        }

    var openglEnabled: Boolean
        get() = prefsSource().getBoolean(
            KEY_OPENGL_ENABLED,
            false
        )
        @SuppressLint("ApplySharedPref")
        set(value) {
            /*
             * Must commit because after this call application will be killed before write succeeds
             */
            prefsSource().edit().putBoolean(KEY_OPENGL_ENABLED, value).commit()
            openglEnabledSubject.onNext(value)
        }

    var openglSupported: Boolean
        get() = prefsSource().getBoolean(
            KEY_OPENGL_SUPPORTED,
            true
        )
        @SuppressLint("ApplySharedPref")
        set(value) {
            /*
             * Must commit because after this call application will be killed before write succeeds
             */
            prefsSource().edit().putBoolean(KEY_OPENGL_SUPPORTED, value).commit()
        }
}
