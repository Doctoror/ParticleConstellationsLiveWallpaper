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
package com.doctoror.particleswallpaper.presentation.di.modules

import android.content.Context
import android.os.Build
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryDefault
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.config.ConfigActivityPresenter
import com.doctoror.particleswallpaper.presentation.config.ConfigActivityPresenterLollipop
import com.doctoror.particleswallpaper.presentation.di.scopes.PerActivity
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by Yaroslav Mytkalyk on 14.06.17.
 *
 * Activity module
 */
@Module
class ActivityModule {

    @PerActivity
    @Provides
    @Named(DEFAULT)
    fun provideDefaultSettings(context: Context):
            SettingsRepository = SettingsRepositoryDefault.getInstance(context.resources!!, context.theme!!)

    @PerActivity
    @Provides
    fun provideConfigActivityPresenter(
            schedulers: SchedulersProvider,
            configurator: SceneConfigurator,
            settings: SettingsRepository): ConfigActivityPresenter =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                ConfigActivityPresenterLollipop(schedulers, configurator, settings)
            else ConfigActivityPresenter(schedulers, configurator, settings)
}
