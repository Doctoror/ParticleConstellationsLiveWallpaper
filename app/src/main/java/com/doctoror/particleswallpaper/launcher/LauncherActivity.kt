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
package com.doctoror.particleswallpaper.launcher

import android.app.Activity
import android.content.Intent
import com.doctoror.particleswallpaper.crashreports.CrashReportingEnabledState
import com.doctoror.particleswallpaper.crashreports.CrashReportsOptInActivity
import com.doctoror.particleswallpaper.crashreports.PrivacySettings
import com.doctoror.particleswallpaper.framework.di.get
import com.doctoror.particleswallpaper.userprefs.ConfigActivity

class LauncherActivity : Activity() {

    override fun onStart() {
        super.onStart()

        val privacySettings: PrivacySettings = get(context = this)

        startActivity(
            Intent(
                this,
                if (privacySettings.crashReportingEnabled == CrashReportingEnabledState.UNRESOVLED) {
                    CrashReportsOptInActivity::class.java
                } else {
                    ConfigActivity::class.java
                }
            )
        )

        finish()
    }
}
