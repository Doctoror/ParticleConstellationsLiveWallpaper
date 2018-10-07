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
package com.doctoror.particleswallpaper.userprefs.optimizetextures

import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.framework.di.scopes.PerPreference
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@PerPreference
class OptimizeTexturesPreferencePresenter @Inject constructor(
    private val glide: Glide,
    private val settings: OpenGlSettings,
    private val schedulers: SchedulersProvider
) {

    private var disposable: Disposable? = null

    private lateinit var view: OptimizeTexturesView

    fun onTakeView(view: OptimizeTexturesView) {
        this.view = view
    }

    fun onStart() {
        disposable = settings.observeOptimizeTextures()
            .observeOn(schedulers.mainThread())
            .subscribe(view::setChecked)
    }

    fun onStop() {
        disposable?.dispose()
    }

    fun onValueChanged(value: Boolean) {
        settings.optimizeTextures = value
        glide.clearMemory()
    }
}
