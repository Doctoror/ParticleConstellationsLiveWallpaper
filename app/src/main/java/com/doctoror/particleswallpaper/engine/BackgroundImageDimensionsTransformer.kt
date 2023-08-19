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

import com.doctoror.particleswallpaper.userprefs.data.NO_URI

/**
 * Usually desired width is always 2 times display width. This is okay when you don't have a
 * background image, but if you do and it's not wide, it will have to be increased in height to
 * match double width. This transformer solves this problem by adjusting dimensions to fit better
 * with background image, if it's set.
 */
class BackgroundImageDimensionsTransformer(
    private val imageAspectRatioLoader: ImageAspectRatioLoader
) {

    fun transform(backgroundUri: String, dimens: EnginePresenter.WallpaperDimensions):
            EnginePresenter.WallpaperDimensions =
        if (backgroundUri == NO_URI) {
            dimens
        } else {
            val imageAspect = imageAspectRatioLoader.load(backgroundUri)
            val dimensAspect = dimens.width.toFloat() / dimens.height.toFloat()
            val desiredAspect = dimens.desiredWidth.toFloat() / dimens.height.toFloat()

            when {
                // Image aspect not loaded, use original dimens
                imageAspect == null -> dimens

                // If image is wider than desiredWidth, just use desiredWidth as is
                imageAspect >= desiredAspect -> dimens

                // If image is still wider than wallpaper width for single display when
                // scaled down, calculate desiredWidth for it
                imageAspect > dimensAspect -> dimens.copy(
                    desiredWidth = (dimens.height.toFloat() * imageAspect).toInt()
                )

                // If reached here, the image is not wider than single display, add just a
                // bit for wallaper scroll so that it doesn't have to be scaled in much
                else -> dimens.copy(desiredWidth = (dimens.width.toFloat() * 1.15f).toInt())
            }
        }
}