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
package com.doctoror.particleswallpaper.presentation.di.components

import android.content.Context
import android.content.pm.PackageManager
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryDevice
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.file.BackgroundImageManager
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.ApplicationlessInjection
import com.doctoror.particleswallpaper.presentation.di.modules.*
import com.doctoror.particleswallpaper.presentation.di.qualifiers.Default
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

    @Default
    fun exposeDefaultSettings(): SettingsRepository

    fun exposeDrawableConfigurator(): SceneConfigurator
    fun exposeMutableSettings(): MutableSettingsRepository

    fun exposePackageManager(): PackageManager
    fun exposePackageName(): String

    fun exposeSchedulers(): SchedulersProvider
    fun exposeSettings(): SettingsRepository
    fun exposeSettingsDevice(): SettingsRepositoryDevice

}
