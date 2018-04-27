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

import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.config.ConfigActivity
import com.doctoror.particleswallpaper.presentation.config.ConfigActivityPresenter
import com.doctoror.particleswallpaper.presentation.config.ConfigActivityPresenterLollipop
import com.doctoror.particleswallpaper.presentation.config.ConfigActivityView
import com.doctoror.particleswallpaper.presentation.di.scopes.PerActivity
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {

    @PerActivity
    @Provides
    fun provideConfigView(activity: ConfigActivity): ConfigActivityView = activity

    @PerActivity
    @Provides
    fun provideConfigActivityPresenter(
            activity: ConfigActivity,
            schedulers: SchedulersProvider,
            configurator: SceneConfigurator,
            requestManager: RequestManager,
            settings: SettingsRepository,
            view: ConfigActivityView
    ): ConfigActivityPresenter =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ConfigActivityPresenterLollipop(
                        activity, schedulers, configurator, requestManager, settings, view)
            } else {
                ConfigActivityPresenter(
                        activity, schedulers, configurator, requestManager, settings, view)
            }

    @PerActivity
    @Provides
    fun provideRequestManager(activity: ConfigActivity) = Glide.with(activity)
}
