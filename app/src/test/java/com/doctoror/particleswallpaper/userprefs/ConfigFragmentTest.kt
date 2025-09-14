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

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.userprefs.bgcolor.BackgroundColorPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.bgimage.BackgroundImagePreference
import com.doctoror.particleswallpaper.userprefs.bgimage.BackgroundImagePreferencePresenter
import com.doctoror.particleswallpaper.userprefs.bgscroll.BackgroundScrollPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.doctoror.particleswallpaper.userprefs.density.DensityPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.engine.EnginePreferencePresenter
import com.doctoror.particleswallpaper.userprefs.engine.EnginePreferenceValueMapper
import com.doctoror.particleswallpaper.userprefs.enginetips.EngineTipsPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.framedelay.FrameDelayPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.howtoapply.HowToApplyPreference
import com.doctoror.particleswallpaper.userprefs.howtoapply.HowToApplyPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.license.LicensePreferencePresenter
import com.doctoror.particleswallpaper.userprefs.linelength.LineLengthPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.linescale.LineScalePreferencePresenter
import com.doctoror.particleswallpaper.userprefs.multisampling.MultisamplingPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.particlecolor.ParticleColorPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.particlescale.ParticleScalePreferencePresenter
import com.doctoror.particleswallpaper.userprefs.particlesscroll.ParticlesScrollPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.performancetips.PerformanceTipsPreferencePresenter
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.userprefs.speedfactor.SpeedFactorPreferencePresenter
import io.reactivex.Observable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.FragmentController
import org.robolectric.annotation.Config

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class ConfigFragmentTest : KoinTest {

    private val underTestController = FragmentController.of(ConfigFragment())

    @Before
    fun setup() {
        stopKoin()

        val deviceSettings: DeviceSettings = mock {
            on(it.observeOpenglSupported()) doReturn Observable.just(false)
        }
        startKoin {
            modules(
                module {
                    single { deviceSettings }
                    single { mock<BackgroundColorPreferencePresenter>() }
                    single { mock<BackgroundImagePreferencePresenter>() }
                    single { mock<BackgroundScrollPreferencePresenter>() }
                    single { mock<DensityPreferencePresenter>() }
                    single { mock<EnginePreferencePresenter>() }
                    single { mock<EnginePreferenceValueMapper>() }
                    single { mock<EngineTipsPreferencePresenter>() }
                    single { mock<FrameDelayPreferencePresenter>() }
                    single { mock<HowToApplyPreferencePresenter>() }
                    single { mock<LicensePreferencePresenter>() }
                    single { mock<LineLengthPreferencePresenter>() }
                    single { mock<LineScalePreferencePresenter>() }
                    single { mock<MultisamplingPreferencePresenter>() }
                    single { mock<OpenChangeWallpaperIntentProvider>() }
                    single { mock<ParticleColorPreferencePresenter>() }
                    single { mock<ParticleScalePreferencePresenter>() }
                    single { mock<ParticlesScrollPreferencePresenter>() }
                    single { mock<PerformanceTipsPreferencePresenter>() }
                    single { mock<SpeedFactorPreferencePresenter>() }
                }
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun backgroundImagePreferenceHostSetOnCreate() {
        val underTest = underTestController.create().start().get()

        val backgroundImagePreference = underTest.findPreference(
            getString(R.string.pref_key_background_image)
        ) as BackgroundImagePreference

        assertEquals(underTest, backgroundImagePreference.fragment)
    }

    @Test
    fun backgroundImagePreferenceHostResetOnDestroy() {
        val underTest = underTestController
            .create()
            .start()
            .stop()
            .destroy()
            .get()

        val backgroundImagePreference = underTest.findPreference(
            getString(R.string.pref_key_background_image)
        ) as BackgroundImagePreference

        assertNull(backgroundImagePreference.fragment)
    }

    @Test
    fun howToApplyPreferenceHostSetOnCreate() {
        val underTest = underTestController.create().start().get()

        val howToApplyPreference = underTest.findPreference(
            getString(R.string.pref_key_how_to_apply)
        ) as HowToApplyPreference

        assertEquals(underTest, howToApplyPreference.fragment)
    }

    @Test
    fun howToApplyPreferenceHostResetOnDestroy() {
        val underTest = underTestController
            .create()
            .start()
            .stop()
            .destroy()
            .get()

        val howToApplyPreference = underTest.findPreference(
            getString(R.string.pref_key_how_to_apply)
        ) as HowToApplyPreference

        assertNull(howToApplyPreference.fragment)
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
