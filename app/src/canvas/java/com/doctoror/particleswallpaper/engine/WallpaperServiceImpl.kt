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
import com.doctoror.particlesdrawable.contract.SceneScheduler
import com.doctoror.particlesdrawable.renderer.CanvasSceneRenderer
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.android.get

class WallpaperServiceImpl : WallpaperService() {

    override fun onCreateEngine(): Engine {
        val renderer = CanvasEngineSceneRenderer(CanvasSceneRenderer(), resources)
        val engine = EngineImpl(renderer)
        renderer.surfaceHolderProvider = engine

        engine.presenter = get(parameters = {
            EngineModuleProvider.makeParameters(
                engine,
                AndroidSchedulers.mainThread(),
                renderer as EngineSceneRenderer,
                engine as SceneScheduler
            )
        })

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
            val desiredWidth = Math.max(width, desiredMinimumWidth)
            val desiredHeight = Math.max(height, desiredMinimumHeight)
            renderer.setDimensions(desiredWidth, desiredHeight)
            presenter.setDimensions(desiredWidth, desiredHeight)
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            renderer.setTranslationX(xPixelOffset.toFloat())
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
