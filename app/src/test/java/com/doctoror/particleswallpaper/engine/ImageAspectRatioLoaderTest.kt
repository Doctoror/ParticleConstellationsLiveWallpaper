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

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.FileNotFoundException

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class ImageAspectRatioLoaderTest {

    private val contentResolver: ContentResolver = mock()

    private val underTest = ImageAspectRatioLoader(contentResolver)

    @Test
    fun returnsNullWhenInputStreamNull() {
        val output = underTest.load("uri")

        assertNull(output)
    }

    @Test
    fun returnsNullWhenOpenInputStreamThrewException() {
        val uri = "uri"
        whenever(contentResolver.openInputStream(Uri.parse(uri))).thenThrow(FileNotFoundException())

        val output = underTest.load("uri")

        assertNull(output)
    }
}