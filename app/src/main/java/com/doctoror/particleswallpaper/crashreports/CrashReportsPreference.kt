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
import android.content.Context
import android.content.Intent
import android.preference.Preference
import android.util.AttributeSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.di.inject
import org.koin.core.parameter.parametersOf

class CrashReportsPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Preference(context, attrs),
    LifecycleObserver,
    CrashReportsPreferenceView {

    private val presenter: CrashReportsPreferencePresenter by inject(
        context = context,
        parameters = { parametersOf(this as CrashReportsPreferenceView) }
    )

    init {
        isPersistent = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        presenter.onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        presenter.onStop()
    }

    override fun setSendCrashReportsEnabled(enabled: Boolean) {
        setSummary(
            if (enabled) {
                R.string.Enabled
            } else {
                R.string.Disabled
            }
        )
    }

    override fun onClick() {
        val intent = Intent(context, CrashReportsOptInActivity::class.java)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
