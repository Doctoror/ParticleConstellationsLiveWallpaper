/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.userprefs.resettodefaults

import com.doctoror.particleswallpaper.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.di.scopes.PerPreference
import javax.inject.Inject

@PerPreference
class ResetToDefaultsPreferencePresenter @Inject constructor(
        private val schedulers: SchedulersProvider,
        private val useCase: ResetToDefaultsUseCase) {

    private lateinit var view: ResetToDefaultsPreferenceView

    fun onTakeView(view: ResetToDefaultsPreferenceView) {
        this.view = view
    }

    fun onClick() {
        view.showWarningDialog()
    }

    fun onResetClick() {
        useCase.action()
                .subscribeOn(schedulers.io())
                .subscribe()
    }
}
