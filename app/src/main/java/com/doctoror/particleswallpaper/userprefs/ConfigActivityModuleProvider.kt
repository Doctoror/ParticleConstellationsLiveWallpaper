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

import android.app.Activity
import android.os.Build
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.framework.app.actions.ActivityStartActivityForResultAction
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentUseCase
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module

private const val PARAM_ACTIVITY = 0
private const val PARAM_SCENE_CONFIGURATION = 1
private const val PARAM_SCENE_CONTROLLER = 2

object ConfigActivityModuleProvider {

    fun createArgumentsMenuPresenter(
        activity: Activity
    ) = parametersOf(activity)

    fun createArgumentsPresenter(
        activity: Activity,
        sceneConfiguration: SceneConfiguration,
        sceneController: SceneController
    ) = parametersOf(activity, sceneConfiguration, sceneController)

    /**
     * Provides the module for wallpaper preview.
     * Parameter list should contain an Activity at index 0.
     */
    fun provide() = module {

        factory { parameterList ->
            ConfigActivityPresenter(
                backgroundLoader = get(),
                configurator = get(),
                sceneConfiguration = parameterList[PARAM_SCENE_CONFIGURATION],
                sceneController = parameterList[PARAM_SCENE_CONTROLLER],
                schedulers = get(),
                settings = get(),
                view = parameterList[PARAM_ACTIVITY]
            )
        }

        factory { parameterList ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ConfigActivityMenuPresenterLollipop(
                    openChangeWallpaperIntentProvider = get(),
                    openChangeWallpaperIntentUseCase = get(parameters = { parameterList }),
                    view = parameterList[PARAM_ACTIVITY]
                )
            } else {
                ConfigActivityMenuPresenterLegacy()
            }
        }

        factory {
            OpenChangeWallpaperIntentUseCase(
                intentProvider = get(),
                startActivityForResultAction = ActivityStartActivityForResultAction(
                    it[PARAM_ACTIVITY]
                )
            )
        }
    }
}
