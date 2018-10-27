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
import com.doctoror.particlesdrawable.contract.SceneScheduler
import com.doctoror.particlesdrawable.opengl.chooser.FailsafeEGLConfigChooserFactory
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer
import com.doctoror.particlesdrawable.opengl.util.GLErrorChecker
import com.doctoror.particleswallpaper.framework.execution.GlScheduler
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import net.rbgrn.android.glwallpaperservice.GLWallpaperService
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class WallpaperServiceImpl : GLWallpaperService() {

    private val settingsOpenGL: OpenGlSettings by inject()

    override fun onCreateEngine(): Engine {
        val renderer = GlEngineSceneRenderer()
        val engine = EngineImpl(renderer, settingsOpenGL.numSamples)

        engine.presenter = get(parameters = {
            EngineModuleProvider.makeParameters(
                engine,
                GlScheduler(engine),
                renderer as EngineSceneRenderer,
                engine as SceneScheduler
            )
        })

        return engine
    }

    inner class EngineImpl(
        private val renderer: GlSceneRenderer,
        samples: Int
    ) : GLEngine(), EngineController, GLSurfaceView.Renderer, SceneScheduler {

        private val handler = Handler()

        lateinit var presenter: EnginePresenter

        private var glErrorCheckerDisabled = false

        init {
            setEGLContextClientVersion(2)
            setEGLConfigChooser(
                FailsafeEGLConfigChooserFactory.newFailsafeEGLConfigChooser(samples, null)
            )
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
            GLErrorChecker.setShouldCheckGlError(true)
            GLErrorChecker.setShouldThrowOnGlError(true)
            renderer.setupGl()
            presenter.onSurfaceCreated()
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            renderer.setDimensions(width, height)
            presenter.setDimensions(width, height)
        }

        override fun onDrawFrame(gl: GL10) {
            presenter.onDrawFrame()

            // Check once and disable after single onDrawFrame to avoid potential crashes after
            // onDestroy
            if (!glErrorCheckerDisabled) {
                glErrorCheckerDisabled = true
                GLErrorChecker.setShouldCheckGlError(false)
                GLErrorChecker.setShouldThrowOnGlError(false)
            }
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
