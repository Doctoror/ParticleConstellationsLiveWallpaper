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
package com.doctoror.particleswallpaper.userprefs.density

import androidx.annotation.VisibleForTesting
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.preference.SeekBarMapper
import com.doctoror.particleswallpaper.framework.preference.SeekBarPreferenceView
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.disposables.Disposable

class DensityPreferencePresenter(
    private val schedulers: SchedulersProvider,
    private val settings: SceneSettings,
    private val view: SeekBarPreferenceView
) : SeekBarMapper<Int> {

    private val seekBarMaxValue = 399
    private var disposable: Disposable? = null

    init {
        view.setMaxInt(seekBarMaxValue)
    }

    fun onPreferenceChange(v: Int?) {
        if (v != null) {
            val value = transformToRealValue(v)
            settings.density = value
        }
    }

    fun onStart() {
        disposable = settings
            .observeDensity()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe { view.setProgressInt(transformToProgress(it)) }
    }

    fun onStop() {
        disposable?.dispose()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun getSeekbarMax() = seekBarMaxValue

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun transformToRealValue(progress: Int) = progress + 1

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun transformToProgress(value: Int) = value - 1

}
