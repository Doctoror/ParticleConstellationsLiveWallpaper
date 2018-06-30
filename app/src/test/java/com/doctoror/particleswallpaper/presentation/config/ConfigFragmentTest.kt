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
package com.doctoror.particleswallpaper.presentation.config

import android.support.annotation.StringRes
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.presentation.preference.BackgroundImagePreference
import com.doctoror.particleswallpaper.presentation.preference.PreviewPreference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.FragmentController
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ConfigFragmentTest {

    private val underTestController = FragmentController.of(ConfigFragment())

    @Test
    fun backgroundImagePreferenceHostSetOnCreate() {
        val underTest = underTestController.create().get()

        val backgroundImagePreference = underTest.findPreference(
                getString(R.string.pref_key_background_image)) as BackgroundImagePreference

        assertEquals(underTest, backgroundImagePreference.host)
    }

    @Test
    fun backgroundImagePreferenceHostResetOnDestroy() {
        val underTest = underTestController
                .create()
                .destroy()
                .get()

        val backgroundImagePreference = underTest.findPreference(
                getString(R.string.pref_key_background_image)) as BackgroundImagePreference

        assertNull(backgroundImagePreference.host)
    }

    @Ignore
    @Test
    fun previewPreferenceHostSetOnCreate() {
        val underTest = underTestController
                .create()
                .get()

        val previewPreference = underTest.findPreference(
                getString(R.string.pref_key_apply)) as PreviewPreference

        assertEquals(underTest, previewPreference.host)
    }

    @Ignore
    @Test
    fun previewPreferenceHostResetOnDestroy() {
        val underTest = underTestController
                .create()
                .destroy()
                .get()

        val previewPreference = underTest.findPreference(
                getString(R.string.pref_key_apply)) as PreviewPreference

        assertNull(previewPreference.host)
    }

    @Test
    fun lifecycleObserversRegisteredOnCreate() {
        val underTest = underTestController
                .create()
                .get()

        assertEquals(8, underTest.lifecycle.observerCount)
    }

    @Test
    fun lifecycleObserversUnregisteredOnDestroy() {
        val underTest = underTestController
                .create()
                .destroy()
                .get()

        assertEquals(0, underTest.lifecycle.observerCount)
    }

    private fun getString(@StringRes key: Int) = RuntimeEnvironment.application.getString(key)
}
