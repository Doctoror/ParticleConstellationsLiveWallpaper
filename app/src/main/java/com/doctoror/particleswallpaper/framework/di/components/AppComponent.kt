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
import android.content.pm.PackageManager
import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.framework.di.ApplicationlessInjection
import com.doctoror.particleswallpaper.framework.di.modules.*
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.file.BackgroundImageManager
import com.doctoror.particleswallpaper.userprefs.data.DefaultSceneSettings
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ActivitiesContributes::class,
    AppModule::class,
    AndroidInjectionModule::class,
    ConfigModule::class,
    FragmentsContributes::class,
    ServicesContributes::class])
interface AppComponent : AndroidInjector<ApplicationlessInjection> {

    fun exposeApiLevelProvider(): ApiLevelProvider

    fun exposeBackgroundImageManager(): BackgroundImageManager
    fun exposeContext(): Context

    fun exposeGlide(): Glide


    fun exposeDrawableConfigurator(): SceneConfigurator

    fun exposePackageManager(): PackageManager
    fun exposePackageName(): String

    fun exposeSchedulers(): SchedulersProvider

    fun exposeSettingsScene(): SceneSettings
    fun exposeSettingsSceneDefaults(): DefaultSceneSettings
    fun exposeSettingsDevice(): DeviceSettings
    fun exposeSettingsOpenGL(): OpenGlSettings

}
