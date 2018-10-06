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
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

const val PREFERENCES_NAME_DEVICE = "prefs_device"
const val KEY_MULTISAMPLING_SUPPORTED = "multisampling_supported"

class SettingsRepositoryDevice(private val prefs: SharedPreferences) {

    private val multisamplingSupportedSubject = BehaviorSubject.create<Boolean>().toSerialized()

    init {
        multisamplingSupportedSubject.onNext(multisamplingSupported)
    }

    fun observeMultisamplingSupported(): Observable<Boolean> = multisamplingSupportedSubject

    var multisamplingSupported
        get() = prefs.getBoolean(KEY_MULTISAMPLING_SUPPORTED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_MULTISAMPLING_SUPPORTED, value).apply()
            multisamplingSupportedSubject.onNext(value)
        }
}
