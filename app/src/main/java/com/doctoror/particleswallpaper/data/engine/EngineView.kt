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
package com.doctoror.particleswallpaper.data.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.VisibleForTesting
import android.util.Log
import com.doctoror.particlesdrawable.ParticlesDrawable

class EngineView(private val surfaceHolderProvider: SurfaceHolderProvider) {

    private val tag = "EngineView"

    val backgroundPaint = Paint()

    val drawable = ParticlesDrawable()

    var background: Drawable? = null

    @VisibleForTesting
    var width = 0
        private set

    @VisibleForTesting
    var height = 0
        private set

    init {
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = Color.BLACK
    }

    fun setBackgroundColor(@ColorInt color: Int) {
        backgroundPaint.color = color
    }

    fun setDimensions(width: Int, height: Int) {
        this.width = width
        this.height = height
        drawable.setBounds(0, 0, width, height)
        background?.setBounds(0, 0, width, height)
    }

    fun start() {
        drawable.start()
    }

    fun stop() {
        drawable.stop()
    }

    fun draw() {
        val holder = surfaceHolderProvider.provideSurfaceHolder()
        var canvas: Canvas? = null
        try {
            canvas = holder.lockCanvas()
            if (canvas != null) {
                drawBackground(canvas)
                drawable.draw(canvas)
                drawable.nextFrame()
            }
        } finally {
            canvas?.let {
                try {
                    holder.unlockCanvasAndPost(it)
                } catch (e: IllegalArgumentException) {
                    Log.wtf(tag, e)
                }
            }
        }
    }

    private fun drawBackground(c: Canvas) {
        val background = background
        if (background == null) {
            drawBackgroundColor(c)
        } else {
            if (background is BitmapDrawable) {
                background.bitmap?.let {
                    if (it.hasAlpha()) {
                        drawBackgroundColor(c)
                    }
                }
            }
            background.draw(c)
        }
    }

    private fun drawBackgroundColor(c: Canvas) {
        c.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
    }
}
