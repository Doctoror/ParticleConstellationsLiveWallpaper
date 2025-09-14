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

import android.app.Fragment
import android.content.Context
import android.preference.ListPreference
import android.util.AttributeSet
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.app.FragmentHolder
import com.doctoror.particleswallpaper.framework.di.inject
import org.koin.core.parameter.parametersOf

class MultisamplingPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ListPreference(context, attrs), MultisamplingPreferenceView, DefaultLifecycleObserver,
    FragmentHolder {

    override var fragment: Fragment? = null

    private val presenter: MultisamplingPreferencePresenter by inject(
        context = context,
        parameters = { parametersOf(this as MultisamplingPreferenceView) }
    )

    private var supported = true

    init {
        isPersistent = false
        entries = emptyArray()
        entryValues = emptyArray()
        onPreferenceChangeListener = OnPreferenceChangeListener { _, v ->
            presenter.onPreferenceChange(
                if (v == null) {
                    0
                } else {
                    (v as CharSequence).toString().toInt()
                }
            )
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

    override fun setPreferenceSupported(supported: Boolean) {
        this.supported = supported
        applySummary()
        isEnabled = supported
    }

    override fun setValue(value: Int) {
        setValue(value.toString())
        applySummary()
    }

    override fun showRestartDialog() {
        fragment?.let {
            MultisamplingRestartDialog().show(it.fragmentManager, "MultisamplingRestartDialog")
        }
    }

    private fun applySummary() {
        summary = if (supported) entry else context.getText(R.string.Unsupported)
    }
}
