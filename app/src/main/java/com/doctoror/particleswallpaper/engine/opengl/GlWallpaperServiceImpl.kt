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
package com.doctoror.particleswallpaper.engine.opengl

import android.annotation.TargetApi
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Handler
import android.view.SurfaceHolder
import com.doctoror.particlesdrawable.contract.SceneScheduler
import com.doctoror.particlesdrawable.opengl.chooser.FailsafeEGLConfigChooserFactory
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer
import com.doctoror.particlesdrawable.opengl.util.GLErrorChecker
import com.doctoror.particleswallpaper.engine.EngineController
import com.doctoror.particleswallpaper.engine.EnginePresenter
import com.doctoror.particleswallpaper.engine.EngineSceneRenderer
import com.doctoror.particleswallpaper.engine.makeInjectArgumentsForWallpaperServiceEngineImpl
import com.doctoror.particleswallpaper.framework.di.get
import com.doctoror.particleswallpaper.framework.execution.GlScheduler
import com.doctoror.particleswallpaper.framework.opengl.KnownOpenglIssuesHandler
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import net.rbgrn.android.glwallpaperservice.GLWallpaperService
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GlWallpaperServiceImpl : GLWallpaperService() {

    override fun onCreateEngine(): Engine {
        val renderer = GlEngineSceneRenderer()

        val settingsOpenGL: OpenGlSettings = get(this)
        val knownOpenglIssuesHandler: KnownOpenglIssuesHandler = get(this)

        val engine = EngineImpl(knownOpenglIssuesHandler, renderer, settingsOpenGL.numSamples)

        engine.presenter = get(
            context = this,
            parameters = {
                makeInjectArgumentsForWallpaperServiceEngineImpl(
                    engine,
                    GlScheduler(engine),
                    renderer as EngineSceneRenderer,
                    engine as SceneScheduler
                )
            }
        )

        return engine
    }

    inner class EngineImpl(
        private val knownOpenglIssuesHandler: KnownOpenglIssuesHandler,
        private val renderer: GlSceneRenderer,
        samples: Int
    ) : GLEngine(), EngineController, GLSurfaceView.Renderer, SceneScheduler {

        private val handler = Handler()

        lateinit var presenter: EnginePresenter

        @Volatile
        private var surfaceWidth = 0

        @Volatile
        private var surfaceHeight = 0

        private var firstDraw = true

        init {
            GLErrorChecker.setShouldCheckGlError(true)
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
            renderer.setupGl()
            presenter.onSurfaceCreated()
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            surfaceWidth = width
            surfaceHeight = height

            notifyDimensions(
                width,
                height,
                desiredMinimumWidth,
                desiredMinimumHeight
            )
        }

        override fun onDesiredSizeChanged(desiredWidth: Int, desiredHeight: Int) {
            super.onDesiredSizeChanged(desiredWidth, desiredHeight)
            notifyDimensions(
                surfaceWidth,
                surfaceHeight,
                desiredWidth,
                desiredHeight
            )
        }

        private fun notifyDimensions(
            surfaceWidth: Int,
            surfaceHeight: Int,
            desiredWidth: Int,
            desiredHeight: Int
        ) {
            if (surfaceWidth != 0 && surfaceHeight != 0) {
                presenter.setDimensions(
                    EnginePresenter.WallpaperDimensions(
                        width = surfaceWidth,
                        height = surfaceHeight,
                        desiredWidth = Math.max(surfaceWidth, desiredWidth),
                        desiredHeight = Math.max(surfaceHeight, desiredHeight)
                    )
                )
            }
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            queueEvent { presenter.setTranslationX(xPixelOffset.toFloat()) }
        }

        override fun onDrawFrame(gl: GL10) {
            if (firstDraw) {
                // Never check draw errors there. Disable on first call.
                GLErrorChecker.setShouldCheckGlError(false)
            }

            presenter.onDrawFrame()

            if (firstDraw) {
                firstDraw = false

                // Check draw error once here, where known issues expected.
                knownOpenglIssuesHandler.handleGlError("GlWallpaperServiceImpl.onDrawFrame")
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
