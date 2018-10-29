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
package com.doctoror.particleswallpaper.userprefs.bgscroll

import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.disposables.CompositeDisposable

class BackgroundScrollPreferencePresenter(
    private val schedulers: SchedulersProvider,
    private val settings: SceneSettings,
    private val view: BackgroundScrollPreferenceView
) {

    private val disposables = CompositeDisposable()

    fun onPreferenceChange(v: Boolean) {
        settings.backgroundScroll = v
    }

    fun onStart() {
        disposables.add(
            settings
                .observeBackgroundScroll()
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribe(view::setChecked)
        )
    }

    fun onStop() {
        disposables.clear()
    }
}
