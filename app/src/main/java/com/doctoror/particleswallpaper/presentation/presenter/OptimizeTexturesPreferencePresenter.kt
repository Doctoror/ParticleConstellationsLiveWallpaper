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

import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.presentation.di.scopes.PerPreference
import com.doctoror.particleswallpaper.presentation.view.OptimizeTexturesView
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@PerPreference
class OptimizeTexturesPreferencePresenter @Inject constructor(
        private val glide: Glide,
        private val settings: MutableSettingsRepository,
        private val schedulers: SchedulersProvider
) : Presenter<OptimizeTexturesView> {

    private var disposable: Disposable? = null

    private lateinit var view: OptimizeTexturesView

    override fun onTakeView(view: OptimizeTexturesView) {
        super.onTakeView(view)
        this.view = view
    }

    fun onStart() {
        disposable = settings.getTextureOptimizationEnabled()
                .observeOn(schedulers.mainThread())
                .subscribe(view::setChecked)
    }

    fun onStop() {
        disposable?.dispose()
    }

    fun onValueChanged(value: Boolean) {
        settings.setTextureOptimizationEnabled(value)
        glide.clearMemory()
    }
}
