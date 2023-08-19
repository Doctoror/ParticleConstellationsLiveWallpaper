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
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class BackgroundImageDimensionsTransformerTest {

    private val imageAspectRatioLoader: ImageAspectRatioLoader = mock()

    private val underTest = BackgroundImageDimensionsTransformer(imageAspectRatioLoader)

    @Test
    fun returnsOriginalDimensionsWhenNoUri() {
        val original = EnginePresenter.WallpaperDimensions(1, 2, 3)

        val output = underTest.transform(NO_URI, original)

        assertEquals(original, output)
        verifyNoInteractions(imageAspectRatioLoader)
    }

    @Test
    fun returnsOriginalDimensionsWhenBackgroundAspectRatioCannotBeLoaded() {
        val original = EnginePresenter.WallpaperDimensions(1, 2, 3)
        val uri = "uri"
        whenever(imageAspectRatioLoader.load(uri)).thenReturn(null)

        val output = underTest.transform(uri, original)

        assertEquals(original, output)
    }

    @Test
    fun returnsOriginalDimensionsWhenBackgroundAspectIsWider() {
        val original = EnginePresenter.WallpaperDimensions(1, 2, 2)
        val uri = "uri"
        whenever(imageAspectRatioLoader.load(uri)).thenReturn(3f / 2f)

        val output = underTest.transform(uri, original)

        assertEquals(original, output)
    }

    @Test
    fun returnsDimensionsWithAdjustedDesiredWidthWhenBackgroundAspectIsWiderThanScreenButNotWiderThanDesiredWidth() {
        val original = EnginePresenter.WallpaperDimensions(2, 4, 4)
        val uri = "uri"
        whenever(imageAspectRatioLoader.load(uri)).thenReturn(3f / 4f)

        val output = underTest.transform(uri, original)

        assertEquals(original.copy(desiredWidth = 3), output)
    }

    @Test
    fun returnsDimensionsWithAdjustedDesiredWidthWhenBackgroundAspectIsWiderThanScreenButNotWiderThanDesiredWidth2() {
        val original = EnginePresenter.WallpaperDimensions(1440, 2560, 5120)
        val uri = "uri"
        whenever(imageAspectRatioLoader.load(uri)).thenReturn(1920f / 1080f)

        val output = underTest.transform(uri, original)

        assertEquals(original.copy(desiredWidth = 4551), output)
    }

    @Test
    fun returnsDimensionsWithAdjustedDesiredWidthWhenBackgroundAspectIsNotWiderThanScreen() {
        val original = EnginePresenter.WallpaperDimensions(20, 50, 50)
        val uri = "uri"
        whenever(imageAspectRatioLoader.load(uri)).thenReturn(20f / 50f)

        val output = underTest.transform(uri, original)

        assertEquals(original.copy(desiredWidth = 23), output)
    }
}