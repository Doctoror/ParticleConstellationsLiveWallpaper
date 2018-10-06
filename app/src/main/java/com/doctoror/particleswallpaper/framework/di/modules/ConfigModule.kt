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
package com.doctoror.particleswallpaper.framework.di.modules

import android.content.Context
import com.doctoror.particleswallpaper.engine.configurator.SceneConfiguratorFactoryImpl
import com.doctoror.particleswallpaper.framework.file.BackgroundImageManagerImpl
import com.doctoror.particleswallpaper.framework.file.FileSaver
import com.doctoror.particleswallpaper.framework.file.FileUriResolver
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.engine.configurator.SceneConfiguratorFactory
import com.doctoror.particleswallpaper.framework.file.BackgroundImageManager
import com.doctoror.particleswallpaper.framework.di.qualifiers.Default
import com.doctoror.particleswallpaper.userprefs.data.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * Provides configuration-related components
 */
@Module
class ConfigModule {

    @Singleton
    @Provides
    @Default
    fun provideDefaultSettings(context: Context):
            SettingsRepository = SettingsRepositoryDefault(context.resources!!, context.theme!!)

    @Provides
    @Singleton
    fun provideDeviceSettings(context: Context) = SettingsRepositoryDevice(
            context.getSharedPreferences(PREFERENCES_NAME_DEVICE, Context.MODE_PRIVATE)
    )

    @Singleton
    @Provides
    fun provideMutableSettings(
            context: Context,
            @Default defaults: SettingsRepository):
            MutableSettingsRepository = SettingsRepositoryImpl(context, defaults)

    @Singleton
    @Provides
    fun provideSettings(settings: MutableSettingsRepository):
            SettingsRepository = settings

    @Provides
    fun provideSceneConfigurator(factory: SceneConfiguratorFactory):
            SceneConfigurator = factory.newSceneConfigurator()

    @Provides
    fun provideSceneConfiguratorFactory(): SceneConfiguratorFactory = SceneConfiguratorFactoryImpl()

    @Provides
    fun provideBackgroundImageManager(context: Context): BackgroundImageManager =
            BackgroundImageManagerImpl(context, FileSaver(context), FileUriResolver(context))

    @Singleton
    @Provides
    fun provideSettingsOpenGL(context: Context) = SettingsRepositoryOpenGL(
            context.getSharedPreferences(PREFERENCES_NAME_OPENGL, Context.MODE_PRIVATE)
    )

}
