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
package com.doctoror.particleswallpaper.userprefs.multisampling

import com.doctoror.particleswallpaper.execution.SchedulersProvider
import com.doctoror.particleswallpaper.settings.SettingsRepositoryDevice
import com.doctoror.particleswallpaper.settings.SettingsRepositoryOpenGL
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MultisamplingPreferencePresenter @Inject constructor(
        private val schedulers: SchedulersProvider,
        private val settings: SettingsRepositoryOpenGL,
        private val settingsDevice: SettingsRepositoryDevice,
        private val wallpaperChecker: WallpaperCheckerUseCase) {

    private lateinit var view: MultisamplingPreferenceView

    private val disposables = CompositeDisposable()

    fun onTakeView(view: MultisamplingPreferenceView) {
        this.view = view
    }

    fun onPreferenceChange(v: Int) {
        settings.numSamples = v

        wallpaperChecker
                .wallpaperInstalledSource()
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
                .observeNumSamples()
                .observeOn(schedulers.mainThread())
                .subscribe(view::setValue))

        disposables.add(settingsDevice
                .observeMultisamplingSupported()
                .observeOn(schedulers.mainThread())
                .subscribe(view::setPreferenceSupported))
    }

    fun onStop() {
        disposables.clear()
    }
}
