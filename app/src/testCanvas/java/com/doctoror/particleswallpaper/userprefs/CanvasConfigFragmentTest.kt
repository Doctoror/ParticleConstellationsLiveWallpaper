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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.FragmentController
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class CanvasConfigFragmentTest {

    private val underTestController = FragmentController.of(ConfigFragment())

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun lifecycleObserversRegisteredOnCreate() {
        val underTest = underTestController
            .create()
            .get()

        assertEquals(8, underTest.lifecycle.observerCount)
    }

    @Test
    fun multisamplingPreferenceDoesNotExist() {
        val underTest = underTestController.create().get()

        val preference = underTest.findPreference(getString(R.string.pref_key_multisampling))

        assertNull(preference)
    }

    @Test
    fun optimizeTexturesPreferenceDoesNotExist() {
        val underTest = underTestController.create().get()

        val preference = underTest.findPreference(getString(R.string.pref_key_optimize_textures))

        assertNull(preference)
    }

    private fun getString(@StringRes key: Int) = RuntimeEnvironment.application.getString(key)
}
