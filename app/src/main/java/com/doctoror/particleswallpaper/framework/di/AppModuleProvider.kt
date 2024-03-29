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
import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.execution.SchedulersProviderImpl
import org.koin.dsl.module.module

fun provideModuleApp(context: Context) = module {

    factory { context.contentResolver }

    single { context }

    single<SchedulersProvider> { SchedulersProviderImpl() }

    factory { Glide.get(context) }

    factory { Glide.with(context) }

    factory { ApiLevelProvider() }
}
