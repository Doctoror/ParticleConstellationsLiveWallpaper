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
package com.doctoror.particleswallpaper.engine.configurator

import android.graphics.Color
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SceneConfiguratorTest {

    private val sceneConfiguration: SceneConfiguration = mock()

    private val sceneSettings: SceneSettings = mock {
        on { it.observeDensity() }.thenReturn(Observable.just(1))
        on { it.observeFrameDelay() }.thenReturn(Observable.just(1))
        on { it.observeLineLength() }.thenReturn(Observable.just(1f))
        on { it.observeLineScale() }.thenReturn(Observable.just(1f))
        on { it.observeParticleColor() }.thenReturn(Observable.just(1))
        on { it.observeParticleScale() }.thenReturn(Observable.just(1f))
        on { it.observeSpeedFactor() }.thenReturn(Observable.just(1f))
    }

    private val underTest = SceneConfigurator(TrampolineSchedulers())

    @Test
    fun subscribes() {

        assertNull(underTest.disposables)

        subscribe()

        assertNotNull(underTest.disposables)
        assertFalse(underTest.disposables!!.isDisposed)

        val disposables = underTest.disposables

        underTest.dispose()

        assertTrue(disposables!!.isDisposed)
        assertNull(underTest.disposables)
    }

    @Test
    fun observesDensity() {
        val subject = PublishSubject.create<Int>()
        whenever(sceneSettings.observeDensity()).thenReturn(subject)

        subscribe()

        subject.onNext(10)
        verify(sceneConfiguration).density = 10

        underTest.setDensityMultiplier(1.2f)
        verify(sceneConfiguration).density = 12

        subject.onNext(32)
        verify(sceneConfiguration).density = 38
    }

    @Test
    fun observesFrameDelay() {
        val values = Observable.just(0, 16)
        whenever(sceneSettings.observeFrameDelay()).thenReturn(values)

        subscribe()

        verify(sceneConfiguration).frameDelay = 0
        verify(sceneConfiguration).frameDelay = 16
    }

    @Test
    fun observesLineLength() {
        val values = Observable.just(64.1f, 32.9f)
        whenever(sceneSettings.observeLineLength()).thenReturn(values)

        subscribe()

        verify(sceneConfiguration).lineLength = 64.1f
        verify(sceneConfiguration).lineLength = 32.9f
    }

    @Test
    fun observesLineScale() {
        val values = Observable.just(8f, 4.1f)
        whenever(sceneSettings.observeLineScale()).thenReturn(values)

        subscribe()

        verify(sceneConfiguration).lineThickness = 8f
        verify(sceneConfiguration).lineThickness = 4.1f
    }

    @Test
    fun observesParticleColor() {
        val colors = Observable.just(Color.CYAN, Color.WHITE)
        whenever(sceneSettings.observeParticleColor()).thenReturn(colors)

        subscribe()

        verify(sceneConfiguration).particleColor = Color.CYAN
        verify(sceneConfiguration).lineColor = Color.CYAN

        verify(sceneConfiguration).particleColor = Color.WHITE
        verify(sceneConfiguration).lineColor = Color.WHITE
    }

    @Test
    fun observesParticleScale() {
        val scales = Observable.just(1f, 6f)
        whenever(sceneSettings.observeParticleScale()).thenReturn(scales)

        subscribe()

        val radiusRange1 = ParticleRadiusMapper.transform(1f)
        verify(sceneConfiguration).setParticleRadiusRange(radiusRange1.first, radiusRange1.second)

        val radiusRange6 = ParticleRadiusMapper.transform(6f)
        verify(sceneConfiguration).setParticleRadiusRange(radiusRange6.first, radiusRange6.second)
    }

    @Test
    fun observesSpeedFactor() {
        val values = Observable.just(0.5f, 13.666f)
        whenever(sceneSettings.observeSpeedFactor()).thenReturn(values)

        subscribe()

        verify(sceneConfiguration).speedFactor = 0.5f
        verify(sceneConfiguration).speedFactor = 13.666f
    }

    private fun subscribe() {
        underTest.subscribe(
            sceneConfiguration,
            mock(),
            sceneSettings,
            Schedulers.trampoline()
        )
    }
}
