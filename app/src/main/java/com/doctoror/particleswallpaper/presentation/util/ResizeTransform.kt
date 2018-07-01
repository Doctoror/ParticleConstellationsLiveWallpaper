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
package com.doctoror.particleswallpaper.presentation.util

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CenterCrop

/**
 * First applies [CenterCrop].
 *
 * Then resizes ignoring proportions.
 *
 * Useful for making OpenGL-efficient POT images.
 */
class CenterCropAndThenResizeTransform(
        private val targetWidth: Int,
        private val targetHeight: Int) : CenterCrop() {

    override fun transform(
            pool: BitmapPool,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int): Bitmap {
        val result = super.transform(pool, toTransform, outWidth, outHeight)
        return Bitmap.createScaledBitmap(result, targetWidth, targetHeight, true)
    }
}
