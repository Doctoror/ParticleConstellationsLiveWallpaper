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

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.doctoror.particleswallpaper.framework.di.inject
import com.doctoror.particleswallpaper.framework.preference.SeekBarPreference
import com.doctoror.particleswallpaper.framework.preference.SeekBarPreferenceView
import org.koin.core.parameter.parametersOf

class DensityPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : SeekBarPreference(context, attrs, defStyle), SeekBarPreferenceView, DefaultLifecycleObserver {

    private val presenter: DensityPreferencePresenter by inject(
        context = context,
        parameters = { parametersOf(this as SeekBarPreferenceView) }
    )

    init {
        isPersistent = false
        setOnPreferenceChangeListener { _, v ->
            presenter.onPreferenceChange(v as Int?)
            true
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        presenter.onStart()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        presenter.onStop()
    }

    override fun setMaxInt(max: Int) {
        this.max = max
    }

    override fun setProgressInt(progress: Int) {
        this.progress = progress
        this.summary = (progress + 1).toString()
    }

    override fun getMaxInt() = max
}
