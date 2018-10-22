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

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.doctoror.particleswallpaper.BuildConfig
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers

class CrashlyticsInitializer(
    private val context: Context,
    private val privacySettings: PrivacySettings
) {

    private val core = DisableableCrashlyticsCore()

    fun initialize() {
        core.enabled = privacySettings.crashReportingEnabled == CrashReportingEnabledState.ENABLED

        Fabric.with(
            context,
            Crashlytics
                .Builder()
                .core(core)
                .build()
        )

        monitorCrashReportingEnabled()
    }

    private fun monitorCrashReportingEnabled() {
        @Suppress("ImplicitThis")
        privacySettings
            .observeCrashReportingEnabled()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                core.enabled = !BuildConfig.DEBUG && it == CrashReportingEnabledState.ENABLED
            }
    }
}
