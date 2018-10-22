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

import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import io.reactivex.disposables.Disposable

class CrashReportsPreferencePresenter(
    private val schedulersProvider: SchedulersProvider,
    private val settings: PrivacySettings,
    private val view: CrashReportsPreferenceView
) {

    private var disposable: Disposable? = null

    fun onStart() {
        disposable = settings
            .observeCrashReportingEnabled()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.mainThread())
            .subscribe { view.setSendCrashReportsEnabled(it == CrashReportingEnabledState.ENABLED) }
    }

    fun onStop() {
        disposable?.dispose()
        disposable = null
    }
}
