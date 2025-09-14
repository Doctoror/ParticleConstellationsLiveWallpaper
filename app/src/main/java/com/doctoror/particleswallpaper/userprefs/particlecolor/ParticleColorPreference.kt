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
package com.doctoror.particleswallpaper.userprefs.particlecolor

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.doctoror.particleswallpaper.framework.di.inject
import org.koin.core.parameter.parametersOf

class ParticleColorPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    ColorPreferenceNoPreview(context, attrs),
    ParticleColorPreferenceView,
    DefaultLifecycleObserver {

    private val presenter: ParticleColorPreferencePresenter by inject(
        context = context,
        parameters = { parametersOf(this as ParticleColorPreferenceView) }
    )

    private var value: Int? = null

    init {
        

        onPreferenceChangeListener = OnPreferenceChangeListener { _, v ->
            presenter.onPreferenceChange(v as Int?)
            true
        }
    }

    @Deprecated("Must declare as deprecated when overriding deprecated api")
    override fun onClick() {
        presenter.onClick()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        presenter.onStart()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        presenter.onStop()
    }

    @Deprecated("Must declare as deprecated when overriding deprecated api")
    override fun getPersistedInt(defaultReturnValue: Int) = this.value ?: defaultReturnValue

    override fun setColor(color: Int) {
        super.setColor(color)
        value = color
    }

    override fun showPreferenceDialog() {
        super.onClick()
    }
}
