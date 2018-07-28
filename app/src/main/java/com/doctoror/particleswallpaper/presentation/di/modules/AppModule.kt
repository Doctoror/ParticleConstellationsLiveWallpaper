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
import android.content.pm.PackageManager
import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.data.execution.SchedulersProviderImpl
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext() = context

    @Provides
    fun provideGlide() = Glide.get(context)

    @Singleton
    @Provides
    fun provideSchedulers(): SchedulersProvider = SchedulersProviderImpl()

    @Singleton
    @Provides
    fun provideApiLevelProvider() = ApiLevelProvider()

    @Provides
    fun providePackageName(): String = context.packageName

    @Provides
    fun providePackageManager(): PackageManager = context.packageManager
}
