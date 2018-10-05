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
package com.doctoror.particleswallpaper.domain.interactor

import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.app.actions.StartActivityForResultAction
import io.reactivex.Single

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 *
 * Opens live wallpaper preview, or wallpaper chooser for pre-Jellybean devices.
 */
class OpenChangeWallpaperIntentUseCase(
        private val intentProvider: OpenChangeWallpaperIntentProvider,
        private val startActivityForResultAction: StartActivityForResultAction)
    : UseCase<Unit> {

    override fun useCase() = Single.fromCallable { action() }!!

    private fun action() {
        val intent = intentProvider.provideActionIntent()
                ?: throw RuntimeException("No supported Intent for preview")
        startActivityForResultAction.startActivityForResult(intent, REQUEST_CODE_CHANGE_WALLPAPER)
    }
}
