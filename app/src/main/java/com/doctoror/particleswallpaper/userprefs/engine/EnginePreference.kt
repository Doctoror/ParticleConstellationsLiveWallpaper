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

import android.app.Fragment
import android.content.Context
import android.preference.ListPreference
import android.util.AttributeSet
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.doctoror.particleswallpaper.framework.app.FragmentHolder
import com.doctoror.particleswallpaper.framework.di.inject
import org.koin.core.parameter.parametersOf

class EnginePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ListPreference(context, attrs), EnginePreferenceView, DefaultLifecycleObserver, FragmentHolder {

    override var fragment: Fragment? = null

    private val valueMapper: EnginePreferenceValueMapper by inject(
        context = context
    )

    private val presenter: EnginePreferencePresenter by inject(
        context = context,
        parameters = { parametersOf(this as EnginePreferenceView) }
    )

    init {
        isPersistent = false
        entries = valueMapper.provideEntries()
        entryValues = valueMapper.provideEntryValues()
        onPreferenceChangeListener = OnPreferenceChangeListener { _, v ->
            presenter.onPreferenceChange(v as CharSequence?)
            false
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

    override fun setValue(value: String) {
        super.setValue(value)
        applySummary()
    }

    override fun showRestartDialog(shouldEnableOpenGl: Boolean) {
        fragment?.let {
            newEngineRestartDialog(shouldEnableOpenGl)
                .show(it.fragmentManager, "MultisamplingRestartDialog")
        }
    }

    private fun applySummary() {
        summary = entry
    }
}
