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
package com.doctoror.particleswallpaper.engine

import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.ScenePresenter
import com.doctoror.particlesdrawable.contract.SceneScheduler
import io.reactivex.Scheduler
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module

private const val PARAM_ENGINE_CONTROLLER = 0
private const val PARAM_RENDER_THREAD_SCHEDULER = 1
private const val PARAM_SCENE_RENDERER = 2
private const val PARAM_SCENE_SCHEDULER = 3

object EngineModuleProvider {

    fun makeParameters(
        controller: EngineController,
        renderThreadScheduler: Scheduler,
        sceneRenderer: EngineSceneRenderer,
        sceneScheduler: SceneScheduler
    ) = parametersOf(
        controller,
        renderThreadScheduler,
        sceneRenderer,
        sceneScheduler
    )

    fun provide() = module {
        factory {
            EngineBackgroundLoader(
                get(),
                get(),
                get(),
                get(),
                TextureDimensionsCalculator()
            )
        }

        factory {
            val scene = ParticlesScene()
            EnginePresenter(
                get(),
                get(),
                get(),
                it[PARAM_ENGINE_CONTROLLER],
                it[PARAM_RENDER_THREAD_SCHEDULER],
                it[PARAM_SCENE_RENDERER],
                get(),
                get(),
                scene,
                ScenePresenter(
                    scene,
                    it[PARAM_SCENE_SCHEDULER],
                    it[PARAM_SCENE_RENDERER]
                )
            )
        }
    }
}
