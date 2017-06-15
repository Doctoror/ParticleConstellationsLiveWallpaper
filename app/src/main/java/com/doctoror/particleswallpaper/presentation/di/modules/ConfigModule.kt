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
import com.doctoror.particleswallpaper.data.config.SceneConfiguratorImpl
import com.doctoror.particleswallpaper.data.file.BackgroundImageManagerImpl
import com.doctoror.particleswallpaper.data.file.FileManager
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryDefault
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryImpl
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.file.BackgroundImageManager
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * Provides configuration-related components
 */
@Module
class ConfigModule {

    companion object {
        const val DEFAULT = "default"
    }

    @Singleton @Provides @Named(DEFAULT) fun provideDefaultSettings(context: Context):
            SettingsRepository = SettingsRepositoryDefault.getInstance(context.resources!!, context.theme!!)

    @Singleton @Provides fun provideMutableSettings(context: Context,
                                                    @Named(DEFAULT) defaults: SettingsRepository):
            MutableSettingsRepository = SettingsRepositoryImpl(context, defaults)

    @Singleton @Provides fun provideSettings(settings: MutableSettingsRepository):
            SettingsRepository = settings

    @Provides fun provideSceneConfigurator(schedulers: SchedulersProvider):
            SceneConfigurator = SceneConfiguratorImpl(schedulers)

    @Provides fun provideBackgroundImageManager(context: Context):
            BackgroundImageManager = BackgroundImageManagerImpl(context, FileManager(context))

}