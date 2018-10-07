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
package com.doctoror.particleswallpaper.framework.di.components

import android.content.Context
import com.doctoror.particleswallpaper.framework.di.modules.AppModule
import com.doctoror.particleswallpaper.framework.di.modules.ConfigModule

object AppComponentProvider {

    private var appComponent: AppComponent? = null

    fun provideAppComponent(context: Context): AppComponent {
        var appComponentLocal = appComponent
        if (appComponentLocal == null) {
            appComponentLocal = DaggerAppComponent
                .builder()
                .appModule(AppModule(context.applicationContext))
                .configModule(ConfigModule())
                .build()!!
            appComponent = appComponentLocal
        }
        return appComponentLocal
    }
}
