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
package com.doctoror.particleswallpaper.framework.file

import android.net.Uri

/**
 * Created by Yaroslav Mytkalyk on 12.06.17.
 *
 * Manages background images retrieved by [android.content.Intent.ACTION_GET_CONTENT].
 * Used for copying them into private files, because the uri permissions cannot be persisted across
 * reboots.
 */
interface BackgroundImageManager {

    /**
     * Copies file from source Uri into backgrounds directory of private files and returns the
     * created private file Uri. All the previous background images are deleted.
     *
     * @throws java.io.IOException in case of errors.
     */
    fun copyBackgroundToFile(source: Uri): Uri

    /**
     * Removes all stored background images
     */
    fun clearBackgroundImage()
}
