package com.doctoror.particleswallpaper.data.engine

import android.graphics.Bitmap
import android.support.annotation.ColorInt

interface CommonSceneRenderer {

    fun markParticleTextureDirty()

    fun setBackgroundTexture(texture: Bitmap?)

    fun setClearColor(@ColorInt color: Int)

    fun recycle()
}
