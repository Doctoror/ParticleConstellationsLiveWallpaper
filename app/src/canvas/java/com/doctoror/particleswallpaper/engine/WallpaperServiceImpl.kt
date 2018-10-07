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
package com.doctoror.particleswallpaper.engine

import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.bumptech.glide.Glide
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.ScenePresenter
import com.doctoror.particlesdrawable.contract.SceneScheduler
import com.doctoror.particlesdrawable.renderer.CanvasSceneRenderer
import com.doctoror.particleswallpaper.engine.configurator.SceneConfiguratorFactory
import com.doctoror.particleswallpaper.framework.di.ApplicationlessInjection
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.doctoror.particleswallpaper.userprefs.data.SettingsRepositoryOpenGL
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class WallpaperServiceImpl : WallpaperService() {

    @Inject
    lateinit var apiLevelProvider: ApiLevelProvider

    @Inject
    lateinit var schedulers: SchedulersProvider

    @Inject
    lateinit var configuratorFactory: SceneConfiguratorFactory

    @Inject
    lateinit var settings: SceneSettings

    @Inject
    lateinit var settingsGl: SettingsRepositoryOpenGL

    private val textureDimensionsCalculator = TextureDimensionsCalculator()

    override fun onCreate() {
        ApplicationlessInjection
            .getInstance(applicationContext)
            .serviceInjector
            .inject(this)
        super.onCreate()
    }

    override fun onCreateEngine(): Engine {
        val scene = ParticlesScene()
        val renderer = CanvasEngineSceneRenderer(CanvasSceneRenderer(), resources)
        val engine = EngineImpl(renderer)
        renderer.surfaceHolderProvider = engine
        val scenePresenter = ScenePresenter(scene, engine, renderer)

        engine.presenter = EnginePresenter(
            apiLevelProvider,
            configuratorFactory.newSceneConfigurator(),
            engine,
            AndroidSchedulers.mainThread(),
            Glide.with(this),
            renderer,
            schedulers,
            settings,
            settingsGl,
            scene,
            scenePresenter,
            textureDimensionsCalculator
        )

        return engine
    }

    inner class EngineImpl(private val renderer: CanvasEngineSceneRenderer) :
        Engine(), EngineController, SceneScheduler, SurfaceHolderProvider {

        private val handler = Handler()

        lateinit var presenter: EnginePresenter

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            presenter.onCreate()
        }

        override fun onDestroy() {
            super.onDestroy()
            presenter.onDestroy()
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            presenter.onSurfaceCreated()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            presenter.setDimensions(width, height)
            renderer.setDimensions(width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            renderer.resetSurfaceCache()
            presenter.visible = false
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            presenter.visible = visible
        }

        override fun provideSurfaceHolder() = surfaceHolder!!

        @TargetApi(Build.VERSION_CODES.O_MR1)
        override fun onComputeColors() = presenter.onComputeColors()

        override fun scheduleNextFrame(delay: Long) {
            if (presenter.visible) {
                handler.postDelayed(renderRunnable, delay)
            }
        }

        override fun unscheduleNextFrame() {
            handler.removeCallbacksAndMessages(null)
        }

        override fun requestRender() {
            presenter.onDrawFrame()
        }

        private val renderRunnable = Runnable { requestRender() }
    }
}
