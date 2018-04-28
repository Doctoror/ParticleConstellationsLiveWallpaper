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
package com.doctoror.particleswallpaper.data.engine

import android.annotation.TargetApi
import android.os.Build
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * Created by Yaroslav Mytkalyk on 18.04.17.
 *
 * The [WallpaperService] implementation.
 */
class WallpaperServiceImpl : WallpaperService() {

    @Inject
    lateinit var apiLevelProvider: ApiLevelProvider

    @Inject
    lateinit var schedulers: SchedulersProvider

    @Inject
    lateinit var configurator: SceneConfigurator

    @Inject
    lateinit var settings: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onCreateEngine(): Engine {
        val engine = EngineImpl()
        engine.presenter = EnginePresenter(
                apiLevelProvider,
                configurator,
                engine,
                Glide.with(this),
                schedulers,
                settings,
                engine,
                EngineView())
        return engine
    }

    inner class EngineImpl : Engine(), EngineController, SurfaceHolderProvider {

        lateinit var presenter: EnginePresenter

        override fun provideSurfaceHolder() = surfaceHolder!!

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            presenter.onCreate()
        }

        override fun onDestroy() {
            super.onDestroy()
            presenter.onDestroy()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            presenter.setDimensions(width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            presenter.visible = false
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            presenter.visible = visible
        }

        @TargetApi(Build.VERSION_CODES.O_MR1)
        override fun onComputeColors() = presenter.onComputeColors()
    }
}
