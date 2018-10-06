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
package com.doctoror.particleswallpaper.userprefs

import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.doctoror.particleswallpaper.config.scene.SceneConfigurator
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentUseCase
import com.doctoror.particleswallpaper.settings.SettingsRepository
import com.doctoror.particleswallpaper.app.actions.ActivityStartActivityForResultAction
import com.doctoror.particleswallpaper.framework.di.scopes.PerActivity
import dagger.Module
import dagger.Provides

@Module
class ConfigActivityModule {

    @PerActivity
    @Provides
    fun provideConfigView(activity: ConfigActivity): ConfigActivityView = activity

    @PerActivity
    @Provides
    fun provideConfigPresenter(
            activity: ConfigActivity,
            schedulers: SchedulersProvider,
            configurator: SceneConfigurator,
            openChangeWallpaperIntentProvider: OpenChangeWallpaperIntentProvider,
            openChangeWallpaperIntentUseCase: OpenChangeWallpaperIntentUseCase,
            requestManager: RequestManager,
            settings: SettingsRepository,
            view: ConfigActivityView
    ): ConfigActivityPresenter =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ConfigActivityPresenterLollipop(
                        activity,
                        schedulers,
                        configurator,
                        openChangeWallpaperIntentProvider,
                        openChangeWallpaperIntentUseCase,
                        requestManager,
                        settings,
                        view)
            } else {
                ConfigActivityPresenter(
                        activity, schedulers, configurator, requestManager, settings, view)
            }

    @PerActivity
    @Provides
    fun provideOpenChangeWallpaperIntentUseCase(
            activity: ConfigActivity,
            intentProvider: OpenChangeWallpaperIntentProvider) =
            OpenChangeWallpaperIntentUseCase(
                    intentProvider,
                    ActivityStartActivityForResultAction(activity))

    @PerActivity
    @Provides
    fun provideRequestManager(activity: ConfigActivity) = Glide.with(activity)
}
