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
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Handler
import android.view.SurfaceHolder
import com.bumptech.glide.Glide
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.ScenePresenter
import com.doctoror.particlesdrawable.contract.SceneScheduler
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer
import com.doctoror.particlesdrawable.opengl.util.MultisampleConfigChooser
import com.doctoror.particleswallpaper.settings.SettingsRepositoryDevice
import com.doctoror.particleswallpaper.settings.SettingsRepositoryOpenGL
import com.doctoror.particleswallpaper.config.app.ApiLevelProvider
import com.doctoror.particleswallpaper.config.scene.SceneConfiguratorFactory
import com.doctoror.particleswallpaper.execution.SchedulersProvider
import com.doctoror.particleswallpaper.settings.SettingsRepository
import com.doctoror.particleswallpaper.app.ApplicationlessInjection
import com.doctoror.particleswallpaper.execution.GlScheduler
import net.rbgrn.android.glwallpaperservice.GLWallpaperService
import javax.inject.Inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class WallpaperServiceImpl : GLWallpaperService() {

    @Inject
    lateinit var apiLevelProvider: ApiLevelProvider

    @Inject
    lateinit var schedulers: SchedulersProvider

    @Inject
    lateinit var configuratorFactory: SceneConfiguratorFactory

    @Inject
    lateinit var settings: SettingsRepository

    @Inject
    lateinit var settingsOpenGL: SettingsRepositoryOpenGL

    @Inject
    lateinit var settingsDevice: SettingsRepositoryDevice

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
        val renderer = GlEngineSceneRenderer()
        val engine = EngineImpl(renderer, settingsOpenGL.observeNumSamples().blockingFirst())
        val scenePresenter = ScenePresenter(scene, engine, renderer)

        engine.presenter = EnginePresenter(
                apiLevelProvider,
                configuratorFactory.newSceneConfigurator(),
                engine,
                GlScheduler(engine),
                Glide.with(this),
                renderer,
                schedulers,
                settings,
                settingsOpenGL,
                scene,
                scenePresenter,
                textureDimensionsCalculator)

        return engine
    }

    inner class EngineImpl(
            private val renderer: GlSceneRenderer,
            samples: Int)
        : GLEngine(), EngineController, GLSurfaceView.Renderer, SceneScheduler {

        private val handler = Handler()

        lateinit var presenter: EnginePresenter

        init {
            if (samples != 0 && settingsDevice.observeMultisamplingSupported().blockingFirst()) {
                setEGLConfigChooser(MultisampleConfigChooser(
                        samples,
                        MultisampleConfigChooser.Callback {
                            // When multisampling requested as 4, it means both 4 and 2 are not supported.
                            // Only then we want to mark this as unsupported.
                            if (samples == 4 && it == 0)
                                settingsDevice.multisamplingSupported = false
                        })
                )
            }
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
            if (presenter.visible) {
                if (delay == 0L) {
                    requestRender()
                } else {
                    handler.postDelayed(renderRunnable, delay)
                }
            }
        }

        override fun unscheduleNextFrame() {
            handler.removeCallbacksAndMessages(null)
        }

        private val renderRunnable = Runnable { requestRender() }
    }
}