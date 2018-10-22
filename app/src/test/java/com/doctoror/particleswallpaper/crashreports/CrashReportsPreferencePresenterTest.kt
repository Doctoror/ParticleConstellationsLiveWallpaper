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

import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.jupiter.api.Test

class CrashReportsPreferencePresenterTest {

    private val settings: PrivacySettings = mock()
    private val view: CrashReportsPreferenceView = mock()

    private val underTest = CrashReportsPreferencePresenter(TrampolineSchedulers(), settings, view)

    @Test
    fun loadsPreferenceDisabledState() {
        whenever(settings.observeCrashReportingEnabled())
            .thenReturn(Observable.just(CrashReportingEnabledState.DISABLED))

        underTest.onStart()

        verify(view).setSendCrashReportsEnabled(false)
    }

    @Test
    fun loadsPreferenceEnabledState() {
        whenever(settings.observeCrashReportingEnabled())
            .thenReturn(Observable.just(CrashReportingEnabledState.ENABLED))

        underTest.onStart()

        verify(view).setSendCrashReportsEnabled(true)
    }
}
