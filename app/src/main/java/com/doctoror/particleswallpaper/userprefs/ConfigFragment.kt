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
package com.doctoror.particleswallpaper.userprefs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceGroup
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleObserver
import com.doctoror.particleswallpaper.BuildConfig
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.app.FragmentHolder
import com.doctoror.particleswallpaper.framework.di.get
import com.doctoror.particleswallpaper.framework.lifecycle.LifecyclePreferenceFragment
import com.doctoror.particleswallpaper.framework.lifecycle.OnActivityResultCallbackHost
import com.doctoror.particleswallpaper.framework.lifecycle.OnActivityResultCallbackHostImpl
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider

@SuppressLint("ValidFragment") // Kotlin constructor with default value generates valid empty java constructor
open class ConfigFragment @JvmOverloads constructor(
    private val ch: OnActivityResultCallbackHostImpl = OnActivityResultCallbackHostImpl()
) :
    LifecyclePreferenceFragment(), OnActivityResultCallbackHost by ch {

    private lateinit var intentProvider: OpenChangeWallpaperIntentProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentProvider = get(context = activity)

        addPreferencesFromResource(R.xml.prefs)
        hideOpenGlPreferencesIfApplicable()
        hidePreviewPreferenceIfCannotStartPreview()
        forEachFragmentHolder(preferenceScreen) { it.fragment = this }
        forEachLifecycleObserver(preferenceScreen) { lifecycle.addObserver(it) }
    }

    private fun hideOpenGlPreferencesIfApplicable() {
        @Suppress("ConstantConditionIf")
        if (!BuildConfig.OPEN_GL_VARIANT) {
            val group = findPreference(getString(R.string.pref_key_performance))
            if (group is PreferenceGroup) {
                group.findPreference(getString(R.string.pref_key_multisampling))?.let {
                    group.removePreference(it)
                }
            }
        }
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_preference, container, false)!!

    override fun onDestroy() {
        super.onDestroy()
        forEachFragmentHolder(preferenceScreen) { it.fragment = null }
        forEachLifecycleObserver(preferenceScreen) { lifecycle.removeObserver(it) }
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

    private fun forEachFragmentHolder(g: PreferenceGroup, c: (FragmentHolder) -> Unit) {
        for (i in 0 until g.preferenceCount) {
            val p = g.getPreference(i)
            if (p is FragmentHolder) {
                c(p)
            }
            if (p is PreferenceGroup) {
                forEachFragmentHolder(p, c)
            }
        }
    }
}
