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
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.*

class FileSaver(private val context: Context) {

    fun saveToPrivateFile(source: Uri, file: File) {
        synchronized(fileLock, { saveToPrivateFileInner(source, file) })
    }

    private fun saveToPrivateFileInner(source: Uri, target: File) {
        if (target.exists()) {
            if (!target.delete()) {
                throw IOException("Failed to delete target file, which already exists: " + target.name)
            }
        }

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var fileDescriptor: ParcelFileDescriptor? = null
        try {
            fileDescriptor = context.contentResolver.openFileDescriptor(source, "r")
            if (fileDescriptor == null) {
                throw IOException("Failed to read input: the FileDescriptor is null")
            }

            ensureAvailableSpace(fileDescriptor.statSize)

            val wrappedFileDescriptor = fileDescriptor.fileDescriptor
                    ?: throw IOException("Failed to read input: the wrapped FileDescriptor is null")

            inputStream = FileInputStream(wrappedFileDescriptor)
            outputStream = FileOutputStream(target)

            inputStream.copyTo(outputStream)
            outputStream.close()
        } finally {
            try {
                fileDescriptor?.close()
            } catch (e: IOException) {
                Log.w(TAG, "Failed to close file descriptor", e)
            }
            try {
                inputStream?.close()
            } catch (e: IOException) {
                Log.w(TAG, "Failed to close input stream", e)
            }
            try {
                outputStream?.close()
            } catch (e: IOException) {
                Log.w(TAG, "Failed to close output stream", e)
            }
        }
    }

    private fun ensureAvailableSpace(required: Long) {
        if (required > 0) {
            val filesDir = context.filesDir ?: throw IOException("getFilesDir() returned null")
            val available = filesDir.freeSpace
            if (available <= required) {
                throw IOException("Not enough availble space. Required: $required, available: $available")
            }
        }
    }

    companion object {
        private val fileLock = Object()
        private const val TAG = "FileSaver"
    }
}
