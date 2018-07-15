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
package com.doctoror.particleswallpaper.domain.interactor

import android.graphics.Color
import com.doctoror.particleswallpaper.domain.file.BackgroundImageManager
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import org.junit.jupiter.api.Test

class ResetToDefaultsUseCaseTest {

    private val defaults: SettingsRepository = mock {
        on(it.getBackgroundColor()).doReturn(Observable.just(0xff212121.toInt()))
        on(it.getBackgroundUri()).doReturn(Observable.just(NO_URI))
        on(it.getDotScale()).doReturn(Observable.just(1.1f))
        on(it.getFrameDelay()).doReturn(Observable.just(1))
        on(it.getLineDistance()).doReturn(Observable.just(86f))
        on(it.getLineScale()).doReturn(Observable.just(1.1f))
        on(it.getNumDots()).doReturn(Observable.just(1))
        on(it.getParticlesColor()).doReturn(Observable.just(Color.WHITE))
        on(it.getStepMultiplier()).doReturn(Observable.just(1.1f))
        on(it.getTextureOptimizationEnabled()).doReturn(Observable.just(true))
    }

    private val settings: MutableSettingsRepository = mock()
    private val backgroundImageManager: BackgroundImageManager = mock()

    private val underTest = ResetToDefaultsUseCase(settings, defaults, backgroundImageManager)

    @Test
    fun setsDefaultBackgroundColor() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setBackgroundColor(defaults.getBackgroundColor().blockingFirst())
    }

    @Test
    fun setsDefaultBackgroundUri() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setBackgroundUri(defaults.getBackgroundUri().blockingFirst())
    }

    @Test
    fun setsDefaultDotScale() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setDotScale(defaults.getDotScale().blockingFirst())
    }

    @Test
    fun setsDefaultFrameDelay() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setFrameDelay(defaults.getFrameDelay().blockingFirst())
    }

    @Test
    fun setsDefaultLineDistance() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setLineDistance(defaults.getLineDistance().blockingFirst())
    }

    @Test
    fun setsDefaultLineScale() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setLineScale(defaults.getLineScale().blockingFirst())
    }

    @Test
    fun setsDefaultNumDots() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setNumDots(defaults.getNumDots().blockingFirst())
    }

    @Test
    fun setsDefaultParticlesColor() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setParticlesColor(defaults.getParticlesColor().blockingFirst())
    }

    @Test
    fun setsDefaultStepMultiplier() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setStepMultiplier(defaults.getStepMultiplier().blockingFirst())
    }

    @Test
    fun clearsBackgroundImage() {
        // When
        underTest.useCase().test()

        // Then
        verify(backgroundImageManager).clearBackgroundImage()
    }

    @Test
    fun setsDefaultTextureOptimizationSettings() {
        // When
        underTest.useCase().test()

        // Then
        verify(settings).setTextureOptimizationEnabled(
                defaults.getTextureOptimizationEnabled().blockingFirst())
    }
}
