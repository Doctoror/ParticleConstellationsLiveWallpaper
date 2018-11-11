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

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.userprefs.bgimage.BackgroundImagePreference
import com.doctoror.particleswallpaper.userprefs.bgimage.BackgroundImagePreferencePresenter
import com.doctoror.particleswallpaper.userprefs.howtoapply.HowToApplyPreference
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.userprefs.preview.PreviewPreference
import com.doctoror.particleswallpaper.userprefs.preview.PreviewPreferencePresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.FragmentController

@RunWith(RobolectricTestRunner::class)
class ConfigFragmentTest : KoinTest {

    private val intentProvider: OpenChangeWallpaperIntentProvider by inject()
    private val underTestController = FragmentController.of(ConfigFragment())

    @Before
    fun setup() {
        declareMock<BackgroundImagePreferencePresenter>()
        declareMock<PreviewPreferencePresenter>()
        declareMock<OpenChangeWallpaperIntentProvider>()
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun backgroundImagePreferenceHostSetOnCreate() {
        val underTest = underTestController.create().get()

        val backgroundImagePreference = underTest.findPreference(
            getString(R.string.pref_key_background_image)
        ) as BackgroundImagePreference

        assertEquals(underTest, backgroundImagePreference.fragment)
    }

    @Test
    fun backgroundImagePreferenceHostResetOnDestroy() {
        val underTest = underTestController
            .create()
            .destroy()
            .get()

        val backgroundImagePreference = underTest.findPreference(
            getString(R.string.pref_key_background_image)
        ) as BackgroundImagePreference

        assertNull(backgroundImagePreference.fragment)
    }

    @Test
    fun howToApplyPreferenceHostSetOnCreate() {
        val underTest = underTestController.create().get()

        val howToApplyPreference = underTest.findPreference(
            getString(R.string.pref_key_how_to_apply)
        ) as HowToApplyPreference

        assertEquals(underTest, howToApplyPreference.fragment)
    }

    @Test
    fun howToApplyPreferenceHostResetOnDestroy() {
        val underTest = underTestController
            .create()
            .destroy()
            .get()

        val howToApplyPreference = underTest.findPreference(
            getString(R.string.pref_key_how_to_apply)
        ) as HowToApplyPreference

        assertNull(howToApplyPreference.fragment)
    }

    @Test
    fun previewPreferenceHostSetOnCreate() {
        whenever(intentProvider.provideActionIntent()).thenReturn(mock())

        val underTest = underTestController
            .create()
            .get()

        val previewPreference = underTest.findPreference(
            getString(R.string.pref_key_apply)
        ) as PreviewPreference

        assertEquals(underTest, previewPreference.fragment)
    }

    @Test
    fun previewPreferenceHostResetOnDestroy() {
        whenever(intentProvider.provideActionIntent()).thenReturn(mock())

        val underTest = underTestController
            .create()
            .destroy()
            .get()

        val previewPreference = underTest.findPreference(
            getString(R.string.pref_key_apply)
        ) as PreviewPreference

        assertNull(previewPreference.fragment)
    }

    @Test
    fun previewPreferenceRemovedWhenIntentIsNull() {
        val underTest = underTestController
            .create()
            .destroy()
            .get()

        val previewPreference = underTest.findPreference(
            getString(R.string.pref_key_apply)
        )

        assertNull(previewPreference)
    }

    @Test
    fun lifecycleObserversUnregisteredOnDestroy() {
        val underTest = underTestController
            .create()
            .destroy()
            .get()

        assertEquals(0, underTest.lifecycle.observerCount)
    }

    private fun getString(@StringRes key: Int) =
        ApplicationProvider.getApplicationContext<Context>().getString(key)
}
