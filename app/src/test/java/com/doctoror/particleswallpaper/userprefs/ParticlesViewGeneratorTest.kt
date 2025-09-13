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
import androidx.test.core.app.ApplicationProvider
import com.doctoror.particlesdrawable.opengl.GlParticlesView
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.util.MultisamplingConfigSpecParser
import com.doctoror.particleswallpaper.framework.util.MultisamplingSupportDetector
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import io.reactivex.Observable
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ParticlesViewGeneratorTest {

    private val context: Context = ApplicationProvider.getApplicationContext<Context>()
    private val deviceSettings: DeviceSettings = mock {
        on(it.observeOpenglEnabled()).thenReturn(Observable.just(true))
    }

    private val multisamplingConfigSpecParser: MultisamplingConfigSpecParser = mock()
    private val multisamplingSupportDetector: MultisamplingSupportDetector = mock()
    private val openGlSettings: OpenGlSettings = mock {
        on(it.observeNumSamples()).thenReturn(Observable.just(0))
    }

    private val underTest = ParticlesViewGenerator(
        context,
        deviceSettings,
        multisamplingConfigSpecParser,
        multisamplingSupportDetector,
        openGlSettings,
        TrampolineSchedulers()
    )

    @After
    fun tearDown() {
        underTest.onStop()
        underTest.onDestroy()
        stopKoin()
    }

    @Test
    fun createsViewWhenNumSamplesLoads() {
        val o = underTest.observeParticlesViewInstance().test()

        underTest.onStart()

        o.assertValue { it is GlParticlesView }
    }

    @Test
    fun createsOneViewIfMultisamplingValuesAreTheSame() {
        whenever(openGlSettings.observeNumSamples())
            .thenReturn(Observable.just(4, 4))

        val o = underTest.observeParticlesViewInstance().test()

        underTest.onStart()

        o.assertValue { it is GlParticlesView }
    }

    @Test
    fun createsViewForEveryValueChange() {
        whenever(openGlSettings.observeNumSamples())
            .thenReturn(Observable.just(4, 4, 2, 0))

        val o = underTest.observeParticlesViewInstance().test()

        underTest.onStart()

        o.assertValueCount(3)
    }
}
