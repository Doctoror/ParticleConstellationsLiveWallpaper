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
package com.doctoror.particleswallpaper.presentation.presenter

import com.doctoror.particleswallpaper.presentation.view.LicensePreferenceView
import javax.inject.Inject

/**
 * Created by Yaroslav Mytkalyk on 05.06.17.
 */
class LicensePreferencePresenter @Inject constructor() : Presenter<LicensePreferenceView> {

    private lateinit var view: LicensePreferenceView

    override fun onTakeView(view: LicensePreferenceView) {
        this.view = view
    }

    override fun onStart() {
        // Stub
    }

    override fun onStop() {
        // Stub
    }

    fun onClick() {
        view.showLicenseDialog()
    }
}