/*
 * Copyright (C) 2023 Yaroslav Mytkalyk
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

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log

class ImageAspectRatioLoader(private val contentResolver: ContentResolver) {

    /**
     * Loads aspect ration of the image from the URI
     *
     * @return aspect ratio or null if cannot be loaded
     */
    fun load(uri: String): Float? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        try {
            contentResolver.openInputStream(Uri.parse(uri)).use {
                BitmapFactory.decodeStream(
                    it,
                    null,
                    options
                )
            }
        } catch (t: Throwable) {
            Log.w(
                "ImageAspectRatioLoader",
                "Failed opening or reading background image",
                t
            )
            return null
        }

        if (options.outWidth == 0 || options.outHeight == 0) {
            Log.w(
                "ImageAspectRatioLoader",
                "Background image bitmap width or height is 0",
            )
            return null
        }

        return options.outWidth.toFloat() / options.outHeight.toFloat()
    }
}