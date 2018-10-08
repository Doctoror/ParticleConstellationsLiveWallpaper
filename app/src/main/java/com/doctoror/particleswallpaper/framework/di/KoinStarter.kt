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
package com.doctoror.particleswallpaper.framework.di

import android.content.Context
import com.doctoror.particleswallpaper.userprefs.ConfigActivityModuleProvider
import com.doctoror.particleswallpaper.userprefs.ConfigModuleProvider
import com.doctoror.particleswallpaper.userprefs.bgcolor.BackgroundColorPreferenceModuleProvider
import com.doctoror.particleswallpaper.userprefs.bgimage.BackgroundImagePreferenceModuleProvider
import com.doctoror.particleswallpaper.userprefs.data.SettingsModuleProvider
import org.koin.standalone.StandAloneContext

class KoinStarter {

    fun startKoin(context: Context) {
        StandAloneContext.startKoin(
            listOf(
                AppModuleProvider().provide(context),
                BackgroundColorPreferenceModuleProvider().provide(),
                BackgroundImagePreferenceModuleProvider().provide(),
                ConfigActivityModuleProvider().provide(),
                ConfigModuleProvider().provide(),
                SettingsModuleProvider().provide()
            )
        )
    }
}
