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
package com.doctoror.particleswallpaper.presentation.presenter

import com.doctoror.particleswallpaper.domain.interactor.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.presentation.di.scopes.PerPreference
import com.doctoror.particleswallpaper.presentation.view.HowToApplyPreferenceView
import javax.inject.Inject

@PerPreference
class HowToApplyPreferencePresenter @Inject constructor(
        private val intentProvider: OpenChangeWallpaperIntentProvider
) : Presenter<HowToApplyPreferenceView> {

    private lateinit var view: HowToApplyPreferenceView

    override fun onTakeView(view: HowToApplyPreferenceView) {
        this.view = view
    }

    fun onClick() {
        val intent = intentProvider.provideActionIntent()
        when {
            intentProvider.isWallaperChooserAction(intent) ->
                view.showDialogHowToApplyUsingChooser()

            intent != null ->
                view.showDialogHowToApplyUsingPreview()

            else ->
                view.showDialogHowToApplyWithoutPreview()
        }
    }
}
