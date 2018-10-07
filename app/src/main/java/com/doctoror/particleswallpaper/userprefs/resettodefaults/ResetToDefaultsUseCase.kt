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
package com.doctoror.particleswallpaper.userprefs.resettodefaults

import com.doctoror.particleswallpaper.framework.file.BackgroundImageManager
import com.doctoror.particleswallpaper.userprefs.data.DefaultSceneSettings
import com.doctoror.particleswallpaper.userprefs.data.MutableSettingsRepository
import com.doctoror.particleswallpaper.userprefs.data.SettingsRepositoryOpenGL
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Resets all configurations to default values.
 */
class ResetToDefaultsUseCase @Inject constructor(
        private val settings: MutableSettingsRepository,
        private val settingsOpenGL: SettingsRepositoryOpenGL,
        private val defaults: DefaultSceneSettings,
        private val backgroundImageManager: BackgroundImageManager) {

    fun action() = Completable.fromAction {
        settings.setBackgroundColor(defaults.backgroundColor)
        settings.setBackgroundUri(defaults.backgroundUri)

        settings.setDotScale(defaults.particleScale)
        settings.setFrameDelay(defaults.frameDelay)

        settings.setLineDistance(defaults.lineLength)
        settings.setLineScale(defaults.lineScale)

        settings.setNumDots(defaults.density)
        settings.setParticlesColor(defaults.particleColor)

        settings.setStepMultiplier(defaults.speedFactor)
        settingsOpenGL.resetToDefaults()

        backgroundImageManager.clearBackgroundImage()
    }!!
}
