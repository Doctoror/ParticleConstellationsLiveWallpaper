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
package com.doctoror.particleswallpaper.presentation.config

import android.annotation.SuppressLint
import android.app.Fragment
import android.arch.lifecycle.LifecycleObserver
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceGroup
import android.view.LayoutInflater
import android.view.ViewGroup
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.interactor.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.presentation.ApplicationlessInjection
import com.doctoror.particleswallpaper.presentation.base.LifecyclePreferenceFragment
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallbackHost
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallbackHostImpl
import com.doctoror.particleswallpaper.presentation.preference.BackgroundImagePreference
import com.doctoror.particleswallpaper.presentation.preference.HowToApplyPreference
import com.doctoror.particleswallpaper.presentation.preference.PreviewPreference
import javax.inject.Inject

/**
 * Created by Yaroslav Mytkalyk on 28.05.17.
 *
 * The config preference screen fragment.
 */
@SuppressLint("ValidFragment") // Kotlin constructor with default value generates valid empty java constructor
open class ConfigFragment @JvmOverloads
constructor(private val ch: OnActivityResultCallbackHostImpl = OnActivityResultCallbackHostImpl())
    : LifecyclePreferenceFragment(), OnActivityResultCallbackHost by ch {

    @Inject
    lateinit var intentProvider: OpenChangeWallpaperIntentProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        ApplicationlessInjection.getInstance(activity!!.applicationContext)
                .fragmentInjector
                .inject(this)

        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.prefs)
        hidePreviewPreferenceIfCannotStartPreview()
        setPreferenceHost(this)
        forEachLifecycleObserver(preferenceScreen) { lifecycle.addObserver(it) }
    }

    private fun hidePreviewPreferenceIfCannotStartPreview() {
        if (intentProvider.provideActionIntent() == null) {
            val p = findPreference(getString(R.string.pref_key_apply))
            if (p != null) {
                preferenceScreen?.removePreference(p)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_preference, container, false)!!

    override fun onDestroy() {
        super.onDestroy()
        setPreferenceHost(null)
        forEachLifecycleObserver(preferenceScreen) { lifecycle.removeObserver(it) }
    }

    private fun setPreferenceHost(host: Fragment?) {
        val backgroundImagePreference = findPreference(getString(R.string.pref_key_background_image))
        if (backgroundImagePreference is BackgroundImagePreference) {
            backgroundImagePreference.host = host
        }

        val previewPreference = findPreference(getString(R.string.pref_key_apply))
        if (previewPreference is PreviewPreference) {
            previewPreference.host = host
        }

        val howToApplyPreference = findPreference(getString(R.string.pref_key_how_to_apply))
        if (howToApplyPreference is HowToApplyPreference) {
            howToApplyPreference.host = host
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ch.callbacks.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    private fun forEachLifecycleObserver(g: PreferenceGroup, c: (LifecycleObserver) -> Unit) {
        for (i in 0 until g.preferenceCount) {
            val p = g.getPreference(i)
            if (p is LifecycleObserver) {
                c(p)
            }
            if (p is PreferenceGroup) {
                forEachLifecycleObserver(p, c)
            }
        }
    }
}
