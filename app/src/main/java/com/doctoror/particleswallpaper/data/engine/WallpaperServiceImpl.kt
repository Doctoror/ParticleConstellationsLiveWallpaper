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
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.bumptech.glide.Glide
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.ScenePresenter
import com.doctoror.particlesdrawable.contract.SceneScheduler
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer
import com.doctoror.particlesdrawable.opengl.util.MultisampleConfigChooser
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.config.SceneConfiguratorFactory
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.ApplicationlessInjection
import com.doctoror.particleswallpaper.scheduler.GlScheduler
import net.rbgrn.android.glwallpaperservice.GLWallpaperService
import javax.inject.Inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Yaroslav Mytkalyk on 18.04.17.
 *
 * The [WallpaperService] implementation.
 */
class WallpaperServiceImpl : GLWallpaperService() {

    @Inject
    lateinit var apiLevelProvider: ApiLevelProvider

    @Inject
    lateinit var schedulers: SchedulersProvider

    @Inject
    lateinit var configuratorFactory: SceneConfiguratorFactory

    @Inject
    lateinit var settings: SettingsRepository

    override fun onCreate() {
        ApplicationlessInjection
                .getInstance(applicationContext)
                .serviceInjector
                .inject(this)
        super.onCreate()
    }

    override fun onCreateEngine(): Engine {
        val scene = ParticlesScene()
        val renderer = GlSceneRenderer()
        val engine = EngineImpl(renderer)
        val scenePresenter = ScenePresenter(scene, renderer, engine)

        engine.presenter = EnginePresenter(
                apiLevelProvider,
                configuratorFactory.newSceneConfigurator(),
                engine,
                GlScheduler(engine),
                Glide.with(this),
                renderer,
                schedulers,
                settings,
                scene,
                scenePresenter)

        return engine
    }

    inner class EngineImpl(private val renderer: GlSceneRenderer)
        : GLEngine(), EngineController, GLSurfaceView.Renderer, SceneScheduler {

        private val handler = Handler()

        lateinit var presenter: EnginePresenter

        init {
            setEGLConfigChooser(MultisampleConfigChooser(4))
            setEGLContextClientVersion(2)
            setRenderer(this)
            renderMode = RENDERMODE_WHEN_DIRTY
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            presenter.onCreate()
        }

        override fun onDestroy() {
            super.onDestroy()
            presenter.onDestroy()
        }

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
            renderer.setupGl()
            presenter.onSurfaceCreated()
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            renderer.setDimensions(width, height)
            presenter.setDimensions(width, height)
        }

        override fun onDrawFrame(gl: GL10) {
            presenter.onDrawFrame()
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

        override fun scheduleNextFrame(delay: Long) {
            if (delay == 0L) {
                requestRender()
            } else {
                handler.postDelayed(renderRunnable, delay)
            }
        }

        override fun unscheduleNextFrame() {
            handler.removeCallbacks(renderRunnable)
        }

        private val renderRunnable = this::requestRender
    }
}
