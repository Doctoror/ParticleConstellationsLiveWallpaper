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
package com.doctoror.particleswallpaper.presentation.preference

import android.app.Fragment
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.preference.ListPreference
import android.util.AttributeSet
import com.doctoror.particleswallpaper.presentation.di.components.AppComponentProvider
import com.doctoror.particleswallpaper.presentation.di.components.DaggerPreferenceComponent
import com.doctoror.particleswallpaper.presentation.dialogs.MultisamplingRestartDialog
import com.doctoror.particleswallpaper.presentation.presenter.NumSamplesPreferencePresenter
import com.doctoror.particleswallpaper.presentation.view.NumSamplesPreferenceView
import javax.inject.Inject

class NumSamplesPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ListPreference(context, attrs), NumSamplesPreferenceView, LifecycleObserver, FragmentHolder {

    override var fragment: Fragment? = null

    @Inject
    lateinit var presenter: NumSamplesPreferencePresenter

    init {
        DaggerPreferenceComponent.builder()
                .appComponent(AppComponentProvider.provideAppComponent(context))
                .build()
                .inject(this)

        isPersistent = false
        presenter.onTakeView(this)
        setOnPreferenceChangeListener { _, v ->
            presenter.onPreferenceChange(if (v == null) {
                0
            } else {
                (v as CharSequence).toString().toInt()
            })
            true
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        presenter.onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        presenter.onStop()
    }

    override fun setValue(value: Int) {
        setValue(value.toString())
        summary = entry
    }

    override fun showRestartDialog() {
        fragment?.let {
            MultisamplingRestartDialog().show(it.fragmentManager, "MultisamplingRestartDialog")
        }
    }
}
