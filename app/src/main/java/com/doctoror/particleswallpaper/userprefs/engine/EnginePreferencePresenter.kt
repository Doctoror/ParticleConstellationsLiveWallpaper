/*
 * Copyright (C) 2019 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.userprefs.engine

import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.util.OpenGlEnabledStateChanger
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.doctoror.particleswallpaper.userprefs.multisampling.WallpaperCheckerUseCase
import io.reactivex.disposables.CompositeDisposable

class EnginePreferencePresenter(
    private val openGlEnabledStateChanger: OpenGlEnabledStateChanger,
    private val schedulers: SchedulersProvider,
    private val settings: DeviceSettings,
    private val valueMapper: EnginePreferenceValueMapper,
    private val view: EnginePreferenceView,
    private val wallpaperChecker: WallpaperCheckerUseCase
) {

    private val disposables = CompositeDisposable()

    fun onPreferenceChange(v: CharSequence?) {
        disposables.add(wallpaperChecker
            .wallpaperInstalledSource()
            .subscribeOn(schedulers.io())
            .map { isInstalled -> isInstalled to settings.openglEnabled }
            .observeOn(schedulers.mainThread())
            .subscribe { (isInstalled, isEnabled) ->
                val shouldEnable = valueMapper.valueToOpenglEnabledState(v)
                if (shouldEnable != isEnabled) {
                    if (isInstalled) {
                        view.showRestartDialog(shouldEnable)
                    } else {
                        openGlEnabledStateChanger.setOpenglGlEnabled(
                            openGlEnabled = shouldEnable,
                            shouldKillApp = false
                        )
                    }
                }
            })
    }

    fun onStart() {
        disposables.add(
            settings
                .observeOpenglEnabled()
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribe { view.setValue(valueMapper.openglEnabledStateToValue(it)) }
        )
    }

    fun onStop() {
        disposables.clear()
    }
}
