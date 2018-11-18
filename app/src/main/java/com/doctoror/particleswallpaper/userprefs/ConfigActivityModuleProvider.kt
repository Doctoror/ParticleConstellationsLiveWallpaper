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
import com.doctoror.particleswallpaper.framework.util.MultisamplingConfigSpecParser
import com.doctoror.particleswallpaper.framework.util.MultisamplingSupportDetector
import com.doctoror.particleswallpaper.framework.view.ViewDimensionsProvider
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentUseCase
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module

private const val MENU_PRESENTER_PARAM_ACTIVITY = 0

private const val PRESENTER_PARAM_SCENE_CONFIGURATION = 0
private const val PRESENTER_PARAM_SCENE_CONTROLLER = 1
private const val PRESENTER_PARAM_VIEW = 2
private const val PRESENTER_PARAM_VIEW_DIMENSIONS_PROVIDER = 3

private const val VIEW_PARAM_ACTIVITY = 0

object ConfigActivityModuleProvider {

    fun createArgumentsMenuPresenter(
        activity: Activity
    ) = parametersOf(activity)

    fun createArgumentsPresenter(
        sceneConfiguration: SceneConfiguration,
        sceneController: SceneController,
        view: SceneBackgroundView,
        viewDimensionsProvider: ViewDimensionsProvider
    ) = parametersOf(sceneConfiguration, sceneController, view, viewDimensionsProvider)

    fun createArgumentsView(
        activity: Activity
    ) = parametersOf(activity)

    /**
     * Provides the module for wallpaper preview.
     * Parameter list should contain an Activity at index 0.
     */
    fun provide() = module {

        factory { parameterList ->
            ConfigActivityPresenter(
                backgroundLoader = get(),
                configurator = get(),
                sceneConfiguration = parameterList[PRESENTER_PARAM_SCENE_CONFIGURATION],
                sceneController = parameterList[PRESENTER_PARAM_SCENE_CONTROLLER],
                schedulers = get(),
                settings = get(),
                view = parameterList[PRESENTER_PARAM_VIEW],
                viewDimensionsProvider = parameterList[PRESENTER_PARAM_VIEW_DIMENSIONS_PROVIDER]
            )
        }

        factory { parameterList ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ConfigActivityMenuPresenterLollipop(
                    openChangeWallpaperIntentProvider = get(),
                    openChangeWallpaperIntentUseCase = get(parameters = { parameterList }),
                    view = parameterList[MENU_PRESENTER_PARAM_ACTIVITY]
                )
            } else {
                ConfigActivityMenuPresenterLegacy()
            }
        }

        factory {
            MultisamplingConfigSpecParser()
        }

        factory {
            MultisamplingSupportDetector(
                get(),
                get()
            )
        }

        factory {
            OpenChangeWallpaperIntentUseCase(
                intentProvider = get(),
                startActivityForResultAction = ActivityStartActivityForResultAction(
                    it[MENU_PRESENTER_PARAM_ACTIVITY]
                )
            )
        }

        factory {
            val activity: Activity = it[VIEW_PARAM_ACTIVITY]
            SceneBackgroundView(get()) { activity.window }
        }

        factory {
            ParticlesViewGenerator(
                it[VIEW_PARAM_ACTIVITY],
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }
}
