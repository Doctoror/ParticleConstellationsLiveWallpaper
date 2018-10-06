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
package com.doctoror.particleswallpaper.userprefs.framedelay

import android.support.annotation.VisibleForTesting
import com.doctoror.particleswallpaper.execution.SchedulersProvider
import com.doctoror.particleswallpaper.settings.MutableSettingsRepository
import com.doctoror.particleswallpaper.presentation.di.scopes.PerPreference
import com.doctoror.particleswallpaper.mapper.SeekBarMapper
import com.doctoror.particleswallpaper.presentation.presenter.Presenter
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@PerPreference
class FrameDelayPreferencePresenter @Inject constructor(
        private val schedulers: SchedulersProvider,
        private val settings: MutableSettingsRepository) : Presenter<FrameDelayPreferenceView>,
        SeekBarMapper<Int> {

    private lateinit var view: FrameDelayPreferenceView

    private val frameDelaySeekbarMin = 16
    private val seekbarMax = 25

    private var disposable: Disposable? = null

    override fun onTakeView(view: FrameDelayPreferenceView) {
        view.setMaxInt(seekbarMax)
        this.view = view
    }

    fun onPreferenceChange(v: Int?) {
        if (v != null) {
            val value = transformToRealValue(v)
            settings.setFrameDelay(value)
            view.setFrameRate(transformToFrameRate(value))
        }
    }

    fun onStart() {
        disposable = settings.getFrameDelay()
                .observeOn(schedulers.mainThread())
                .subscribe {
                    val progress = transformToProgress(it)
                    view.setProgressInt(progress)
                    view.setFrameRate(transformToFrameRate(transformToRealValue(progress)))
                }
    }

    fun onStop() {
        disposable?.dispose()
    }

    private fun transformToFrameRate(value: Int) = if (value == 0) {
        60
    } else {
        Math.min(1000 / value, 60)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun getSeekbarMax() = seekbarMax

    /**
     * The seek bar represents frame rate as percentage.
     * Converts the seek bar value between 0 and 30 to percent and then the percentage to a
     * frame delay, where
     * 16 ms = 100%
     * 41 ms = 0%
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun transformToRealValue(progress: Int): Int {
        var value = (frameDelaySeekbarMin.toFloat() + view.getMaxInt().toFloat() *
                (1f - progress.toFloat() / view.getMaxInt().toFloat())).toInt()
        if (value <= 16) {
            value = 0
        }
        return value
    }

    /**
     * Converts frame delay to seek bar frame rate.
     * @see transformToRealValue
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun transformToProgress(value: Int): Int {
        if (value == 0) {
            return seekbarMax
        }
        val percent = (value.toFloat() - frameDelaySeekbarMin.toFloat()) / view.getMaxInt().toFloat()
        return ((1f - percent) * view.getMaxInt().toFloat()).toInt()
    }
}
