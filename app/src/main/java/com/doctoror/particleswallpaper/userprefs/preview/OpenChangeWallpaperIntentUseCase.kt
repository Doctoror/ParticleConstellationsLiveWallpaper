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
package com.doctoror.particleswallpaper.userprefs.preview

import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.framework.app.actions.StartActivityForResultAction
import io.reactivex.Completable

/**
 * Opens live wallpaper preview, or wallpaper chooser for pre-Jellybean devices.
 */
class OpenChangeWallpaperIntentUseCase(
        private val intentProvider: OpenChangeWallpaperIntentProvider,
        private val startActivityForResultAction: StartActivityForResultAction) {

    fun action() = Completable.fromAction {
        val intent = intentProvider.provideActionIntent()
                ?: throw RuntimeException("No supported Intent for preview")
        startActivityForResultAction.startActivityForResult(intent, REQUEST_CODE_CHANGE_WALLPAPER)
    }!!
}
