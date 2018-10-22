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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.di.get
import com.doctoror.particleswallpaper.userprefs.ConfigActivity

class CrashReportsOptInActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_reports)

        val privacySettings: PrivacySettings = get(context = this)

        findViewById<View>(R.id.btnAllow)
            .setOnClickListener {
                privacySettings.crashReportingEnabled = CrashReportingEnabledState.ENABLED
                goToConfigAndFinish()
            }

        findViewById<View>(R.id.btnNo)
            .setOnClickListener {
                privacySettings.crashReportingEnabled = CrashReportingEnabledState.DISABLED
                goToConfigAndFinish()
            }
    }

    private fun goToConfigAndFinish() {
        startActivity(Intent(this, ConfigActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        // Disable back action
    }
}
