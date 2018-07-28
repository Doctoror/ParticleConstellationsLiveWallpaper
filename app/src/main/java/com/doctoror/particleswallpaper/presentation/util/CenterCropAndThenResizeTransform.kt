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
import android.graphics.Matrix
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.TransformationUtils

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
        if (toTransform.width == targetWidth && toTransform.height == targetHeight) {
            return toTransform
        }

        val matrix = Matrix()
        // Applies center crop to create a normal image
        applyCenterCrop(toTransform, outWidth, outHeight, matrix)

        // Stretches to meed POT requirement
        applyPostScale(outWidth, outHeight, matrix)

        val result = pool.get(targetWidth, targetHeight, getNonNullConfig(toTransform))
        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        TransformationUtils.setAlpha(toTransform, result)

        com.doctoror.particleswallpaper.presentation.util.TransformationUtils
                .applyMatrix(toTransform, result, matrix)

        return result
    }

    private fun applyCenterCrop(
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int,
            matrix: Matrix) {
        if (toTransform.width != outWidth || toTransform.height == outHeight) {
            // From ImageView/Bitmap.createScaledBitmap.
            val scale: Float
            val dx: Float
            val dy: Float
            if (toTransform.width * outHeight > outWidth * toTransform.height) {
                scale = outHeight.toFloat() / toTransform.height.toFloat()
                dx = (outWidth - toTransform.width * scale) * 0.5f
                dy = 0f
            } else {
                scale = outWidth.toFloat() / toTransform.width.toFloat()
                dx = 0f
                dy = (outHeight - toTransform.height * scale) * 0.5f
            }

            matrix.setScale(scale, scale)
            matrix.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
        }
    }

    private fun applyPostScale(
            outWidth: Int,
            outHeight: Int,
            matrix: Matrix
    ) {
        val scaleX = targetWidth.toFloat() / outWidth.toFloat()
        val scaleY = targetHeight.toFloat() / outHeight.toFloat()
        matrix.postScale(scaleX, scaleY)
    }

    private fun getNonNullConfig(bitmap: Bitmap): Bitmap.Config {
        return if (bitmap.config != null) bitmap.config else Bitmap.Config.ARGB_8888
    }
}
