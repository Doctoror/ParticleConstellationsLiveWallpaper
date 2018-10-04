package com.doctoror.particleswallpaper.data.engine

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.VisibleForTesting
import android.util.Log
import android.view.SurfaceHolder
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.renderer.CanvasSceneRenderer
import com.doctoror.particlesdrawable.renderer.DefaultSceneRenderer
import com.doctoror.particleswallpaper.data.config.HardwareCanvasBlacklist

class CanvasEngineSceneRenderer(
        private val canvasSceneRenderer: CanvasSceneRenderer,
        private val resources: Resources
) : DefaultSceneRenderer(canvasSceneRenderer), EngineSceneRenderer {

    private val tag = "EngineView"

    private val backgroundPaint = Paint()

    private var background: Drawable? = null

    var surfaceHolderProvider: SurfaceHolderProvider? = null

    @VisibleForTesting
    var width = 0
        private set

    @VisibleForTesting
    var height = 0
        private set

    @JvmField
    @VisibleForTesting
    var surfaceHolder: SurfaceHolder? = null

    init {
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = Color.BLACK
    }

    fun setDimensions(width: Int, height: Int) {
        this.width = width
        this.height = height
        background?.setBounds(0, 0, width, height)
        surfaceHolder = null
    }

    fun resetSurfaceCache() {
        surfaceHolder = null
    }

    override fun markParticleTextureDirty() {
        // Do nothing
    }

    override fun setBackgroundTexture(texture: Bitmap?) {
        background = if (texture == null) {
            null
        } else {
            BitmapDrawable(resources, texture).apply {
                setBounds(0, 0, width, height)
            }
        }
    }

    override fun setClearColor(@ColorInt color: Int) {
        backgroundPaint.color = color
    }

    override fun recycle() {
        background = null
    }

    override fun drawScene(scene: ParticlesScene) {
        var holder = surfaceHolder
        if (holder == null) {
            holder = surfaceHolderProvider?.provideSurfaceHolder()
                    ?: throw IllegalStateException("SurfaceHolderProvider not set")
            surfaceHolder = holder
        }
        var canvas: Canvas? = null
        try {
            canvas = lockCanvas(holder)
            if (canvas != null) {
                drawBackground(canvas)
                canvasSceneRenderer.setCanvas(canvas)
                super.drawScene(scene)
                canvasSceneRenderer.setCanvas(null)
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

    // Inline for avoiding extra method call in draw
    @Suppress("NOTHING_TO_INLINE")
    private inline fun canLockHardwareCanvas() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !HardwareCanvasBlacklist.isBlacklistedForLockHardwareCanvas(Build.DEVICE, Build.PRODUCT)

    // Inline for avoiding extra method call in draw
    @Suppress("NOTHING_TO_INLINE")
    private inline fun drawBackground(c: Canvas) {
        val background = background
        if (background == null || isBackgroundRecycled()) {
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

    // Inline for avoiding extra method call in draw
    @Suppress("NOTHING_TO_INLINE")
    private inline fun isBackgroundRecycled(): Boolean {
        val background = background
        return if (background is BitmapDrawable) {
            background.bitmap?.isRecycled ?: false
        } else {
            false
        }
    }

    // Inline for avoiding extra method call in draw
    @Suppress("NOTHING_TO_INLINE")
    private fun drawBackgroundColor(c: Canvas) {
        c.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
    }

    // Inline for avoiding extra method call in draw
    @Suppress("NOTHING_TO_INLINE")
    private inline fun lockCanvas(holder: SurfaceHolder): Canvas? =
            if (canLockHardwareCanvas()) {
                holder.lockHardwareCanvas()
            } else {
                holder.lockCanvas()
            }
}
