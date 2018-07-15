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

import com.doctoror.particleswallpaper.domain.file.BackgroundImageManager
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.di.qualifiers.Default
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 *
 * Resets all configurations to default values.
 */
class ResetToDefaultsUseCase @Inject constructor(
        private val settings: MutableSettingsRepository,
        @Default private val defaults: SettingsRepository,
        private val backgroundImageManager: BackgroundImageManager) : UseCase<Unit> {

    override fun useCase() = Single.fromCallable { reset() }!!

    private fun reset() {
        settings.setBackgroundUri(defaults.getBackgroundUri().blockingFirst())
        settings.setBackgroundColor(defaults.getBackgroundColor().blockingFirst())

        settings.setDotScale(defaults.getDotScale().blockingFirst())
        settings.setFrameDelay(defaults.getFrameDelay().blockingFirst())

        settings.setLineDistance(defaults.getLineDistance().blockingFirst())
        settings.setLineScale(defaults.getLineScale().blockingFirst())

        settings.setNumDots(defaults.getNumDots().blockingFirst())
        settings.setNumSamples(defaults.getNumSamples().blockingFirst())
        settings.setParticlesColor(defaults.getParticlesColor().blockingFirst())

        settings.setStepMultiplier(defaults.getStepMultiplier().blockingFirst())
        settings.setTextureOptimizationEnabled(
                defaults.getTextureOptimizationEnabled().blockingFirst())

        backgroundImageManager.clearBackgroundImage()
    }
}
