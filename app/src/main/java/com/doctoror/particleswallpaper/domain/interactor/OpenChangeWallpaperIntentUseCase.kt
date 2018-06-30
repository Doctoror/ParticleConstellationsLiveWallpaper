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

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.util.Log
import com.doctoror.particleswallpaper.data.engine.WallpaperServiceImpl
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_CHANGE_WALLPAPER
import io.reactivex.Single

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 *
 * Opens live wallpaper preview, or wallpaper chooser for pre-Jellybean devices.
 */
class OpenChangeWallpaperIntentUseCase(
        private val apiLevelProvider: ApiLevelProvider,
        private val packageName: String,
        private val startActivityForResultAction: StartActivityForResultAction)
    : UseCase<Boolean> {

    private val tag = "OpenChangeWpUseCase"

    override fun useCase() = Single.fromCallable { action() }!!

    @SuppressLint("InlinedApi")
    private fun action(): Boolean {
        val intent = Intent()
        if (apiLevelProvider.provideSdkInt() >= Build.VERSION_CODES.JELLY_BEAN) {
            intent.action = WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
            intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true)
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(packageName, WallpaperServiceImpl::class.java.canonicalName))
        } else {
            intent.action = WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER
        }
        return try {
            startActivityForResultAction.startActivityForResult(intent, REQUEST_CODE_CHANGE_WALLPAPER)
            true
        } catch (e: ActivityNotFoundException) {
            Log.w(tag, e)
            false
        }
    }
}
