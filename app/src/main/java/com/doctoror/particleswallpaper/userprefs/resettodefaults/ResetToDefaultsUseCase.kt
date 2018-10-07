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
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Resets all configurations to default values.
 */
class ResetToDefaultsUseCase @Inject constructor(
        private val defaults: DefaultSceneSettings,
        private val settings: SceneSettings,
        private val settingsOpenGL: OpenGlSettings,
        private val backgroundImageManager: BackgroundImageManager) {

    fun action() = Completable.fromAction {
        settings.backgroundColor = defaults.backgroundColor
        settings.backgroundUri = defaults.backgroundUri

        settings.density = defaults.density
        settings.frameDelay = defaults.frameDelay

        settings.lineLength = defaults.lineLength
        settings.lineScale = defaults.lineScale

        settings.particleColor = defaults.particleColor
        settings.particleScale = defaults.particleScale

        settings.speedFactor = defaults.speedFactor
        settingsOpenGL.resetToDefaults()

        backgroundImageManager.clearBackgroundImage()
    }!!
}
