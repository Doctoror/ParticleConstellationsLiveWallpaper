/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.data.file

import android.content.Context
import android.net.Uri
import android.support.v4.content.FileProvider
import android.util.Log
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.file.BackgroundImageManager
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

/**
 * The background image manager
 */
class BackgroundImageManagerImpl(
        private val context: Context,
        private val fileManager: FileManager) : BackgroundImageManager {

    private val backgroundsDirectory = "backgrounds"
    private val fileNamePrefix = "bg"
    private val tag = "BackgroundImageManager"

    private val fileNamePattern = Pattern.compile("$fileNamePrefix([0-9])+")!!

    override fun copyBackgroundToFile(source: Uri): Uri {
        val filesDir = context.filesDir ?: throw IOException("getFilesDir() returned null")
        val backgroundsDir = File(filesDir, backgroundsDirectory)
        if (!backgroundsDir.exists()) {
            if (!backgroundsDir.mkdir()) {
                throw IOException("Failed to create backgrounds directory")
            }
        }
        val largestIndex = deletePreviousFilesAndGetLargestFileIndex(backgroundsDir)

        val file = File(backgroundsDir, fileNamePrefix + largestIndex)
        fileManager.saveToPrivateFile(source, file)

        if (!file.exists()) {
            throw IOException("The created file does not exist")
        }

        return FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), file)
                ?: throw IOException("FileProvider returned null Uri for saved file")
    }

    private fun deletePreviousFilesAndGetLargestFileIndex(filesDir: File): Int {
        var largestIndex = 0
        val files = filesDir.listFiles()
        files?.forEach {
            try {
                val matcher = fileNamePattern.matcher(it.name)
                if (matcher.matches()) {
                    if (!it.delete()) {
                        Log.w(tag, "Failed to delete previous background image file named $it")
                    }

                    val number = matcher.group(1).toInt()
                    if (largestIndex < number) {
                        largestIndex = number
                    }
                }
            } catch (e: NumberFormatException) {
                Log.wtf(tag, e)
            }
        }
        return largestIndex + 1
    }

    override fun clearBackgroundImage() {
        val filesDir = context.filesDir
        if (filesDir == null) {
            Log.w(tag, "getFilesDir() returned null")
            return
        }
        val backgroundsDir = File(filesDir, backgroundsDirectory)
        if (backgroundsDir.exists()) {
            val files = backgroundsDir.listFiles()
            files?.forEach {
                if (!it.delete()) {
                    Log.w(tag, "Failed to delete background image file: " + it.name)
                }
            }
        }
    }
}
