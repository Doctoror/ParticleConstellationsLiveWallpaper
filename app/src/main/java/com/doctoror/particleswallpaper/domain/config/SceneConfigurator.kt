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
package com.doctoror.particleswallpaper.domain.config

import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.Scheduler

/**
 * Created by Yaroslav Mytkalyk on 29.05.17.
 *
 * Implementation should monitor for [SettingsRepository] changes and configure [ParticlesScene]
 * based on the settings.
 */
interface SceneConfigurator {

    fun subscribe(
            configuration: SceneConfiguration,
            controller: SceneController,
            settings: SettingsRepository,
            scheduler: Scheduler)

    fun dispose()
}
