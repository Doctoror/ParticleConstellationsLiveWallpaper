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
package com.doctoror.particleswallpaper.framework.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class BackgroundImageManagerImplTest {

    private val appContext = RuntimeEnvironment.application
    private val filesDir = appContext.filesDir

    private val sourceFileUri = Uri.parse("content://source")
    private val targetFileUri = Uri.parse("content://target")

    private val sourceFile = File(filesDir, "sourceFile")
    private val expectedFile1 = File(filesDir, "backgrounds/bg1")
    private val expectedFile2 = File(filesDir, "backgrounds/bg2")

    private val fileUriResolver: FileUriResolver = mock {
        on(it.getUriForFile(expectedFile1)).doReturn(targetFileUri)
        on(it.getUriForFile(expectedFile2)).doReturn(targetFileUri)
    }

    private val fileContents = byteArrayOf(0, 1, 2)

    private lateinit var underTest: BackgroundImageManagerImpl

    @Before
    fun setup() {
        sourceFile.writeBytes(fileContents)
        underTest = BackgroundImageManagerImpl(
                appContext,
                FileSaver(mockContextForSourceFile()),
                fileUriResolver)
    }

    @After
    fun tearDown() {
        sourceFile.delete()
        expectedFile1.delete()
        expectedFile2.delete()
    }

    @Test
    fun copiesBackgroundToFile() {
        // When
        underTest.copyBackgroundToFile(sourceFileUri)

        // Then
        val readFileContents = expectedFile1.readBytes()
        assertTrue(readFileContents.contentEquals(fileContents))
    }

    @Test
    fun overridesSecondBackgroundWithDifferentNameAndDeletesPrevious() {
        // When
        underTest.copyBackgroundToFile(sourceFileUri)
        underTest.copyBackgroundToFile(sourceFileUri)

        // Then
        assertFalse(expectedFile1.exists())

        val readFileContents = expectedFile2.readBytes()
        assertTrue(readFileContents.contentEquals(fileContents))
    }

    @Test
    fun deletesAllBackgrounds() {
        // When
        underTest.copyBackgroundToFile(sourceFileUri)
        underTest.copyBackgroundToFile(sourceFileUri)
        underTest.clearBackgroundImage()

        // Then
        assertFalse(expectedFile1.exists())
        assertFalse(expectedFile2.exists())
    }

    private fun mockContextForSourceFile(): Context {
        val contentResolver: ContentResolver = mock {
            on(it.openFileDescriptor(sourceFileUri, "r")).doAnswer {
                ParcelFileDescriptor.open(sourceFile, ParcelFileDescriptor.MODE_READ_ONLY)
            }
        }

        return mock {
            on(it.contentResolver).doReturn(contentResolver)
            on(it.filesDir).doReturn(filesDir)
        }
    }
}
