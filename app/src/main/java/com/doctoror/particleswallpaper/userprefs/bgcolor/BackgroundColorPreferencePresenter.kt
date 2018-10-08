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
package com.doctoror.particleswallpaper.userprefs.bgcolor

import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.data.DefaultSceneSettings
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.disposables.Disposable

class BackgroundColorPreferencePresenter(
    private val schedulers: SchedulersProvider,
    private val settings: SceneSettings,
    private val defaults: DefaultSceneSettings,
    private val view: BackgroundColorPreferenceView
) {

    private var disposable: Disposable? = null

    fun onPreferenceChange(v: Int?) {
        val color = v ?: defaults.backgroundColor
        settings.backgroundColor = color
    }

    fun onStart() {
        disposable = settings.observeBackgroundColor()
            .observeOn(schedulers.mainThread())
            .subscribe { view.setColor(it) }
    }

    fun onStop() {
        disposable?.dispose()
    }
}
