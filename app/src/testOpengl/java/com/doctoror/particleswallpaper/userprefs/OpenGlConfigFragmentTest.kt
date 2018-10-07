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
package com.doctoror.particleswallpaper.userprefs

import androidx.annotation.StringRes
import com.doctoror.particleswallpaper.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.FragmentController
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class OpenGlConfigFragmentTest {

    private val underTestController = FragmentController.of(ConfigFragment())

    @Test
    fun lifecycleObserversRegisteredOnCreate() {
        val underTest = underTestController
            .create()
            .get()

        assertEquals(10, underTest.lifecycle.observerCount)
    }

    @Test
    fun multisamplingPreferenceDoesNotExist() {
        val underTest = underTestController.create().get()

        val preference = underTest.findPreference(getString(R.string.pref_key_multisampling))

        assertNotNull(preference)
    }

    @Test
    fun optimizeTexturesPreferenceDoesNotExist() {
        val underTest = underTestController.create().get()

        val preference = underTest.findPreference(getString(R.string.pref_key_optimize_textures))

        assertNotNull(preference)
    }

    private fun getString(@StringRes key: Int) = RuntimeEnvironment.application.getString(key)
}
