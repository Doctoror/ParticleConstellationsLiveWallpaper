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

import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryDevice
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.interactor.WallpaperCheckerUseCase
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.presentation.view.NumSamplesPreferenceView
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class NumSamplesPreferencePresenter @Inject constructor(
        private val schedulers: SchedulersProvider,
        private val settings: MutableSettingsRepository,
        private val settingsDevice: SettingsRepositoryDevice,
        private val wallpaperChecker: WallpaperCheckerUseCase) : Presenter<NumSamplesPreferenceView> {

    private lateinit var view: NumSamplesPreferenceView

    private val disposables = CompositeDisposable()

    override fun onTakeView(view: NumSamplesPreferenceView) {
        this.view = view
    }

    fun onPreferenceChange(v: Int) {
        settings.setNumSamples(v)

        wallpaperChecker
                .useCase()
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribe { isInstalled ->
                    if (isInstalled) {
                        view.showRestartDialog()
                    }
                }
    }

    fun onStart() {
        disposables.add(settings
                .getNumSamples()
                .observeOn(schedulers.mainThread())
                .subscribe(view::setValue))

        disposables.add(settingsDevice
                .getMultisamplingSupported()
                .observeOn(schedulers.mainThread())
                .subscribe(view::setPreferenceSupported))
    }

    fun onStop() {
        disposables.clear()
    }
}
