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
package com.doctoror.particleswallpaper.userprefs.data

import android.content.Context
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import org.koin.dsl.module.module

class SettingsModuleProvider {

    fun provide() = module {

        factory {
            DefaultSceneSettings(
                get<Context>().resources!!,
                get<Context>().theme!!
            )
        }

        single {
            DeviceSettings(
                prefsSource = {
                    get<Context>().getSharedPreferences(
                        PREFERENCES_NAME_DEVICE,
                        Context.MODE_PRIVATE
                    )
                }
            )
        }

        single {
            OpenGlSettings(
                prefsSource = {
                    get<Context>().getSharedPreferences(
                        PREFERENCES_NAME_OPENGL,
                        Context.MODE_PRIVATE
                    )
                }
            )
        }

        factory { SceneConfigurator(get()) }

        single {
            SceneSettings(
                defaults = get(),
                prefsSource = {
                    get<Context>().getSharedPreferences(
                        PREFERENCES_NAME_SCENE,
                        Context.MODE_PRIVATE
                    )
                }
            )
        }
    }
}
