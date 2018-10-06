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

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.VisibleForTesting
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.engine.WallpaperServiceImpl
import javax.inject.Inject

class OpenChangeWallpaperIntentProvider @Inject constructor(
        private val apiLevelProvider: ApiLevelProvider,
        private val packageManager: PackageManager,
        private val packageName: String
) {

    /**
     * Provides Intent for live wallpaper preview.
     *
     * Returns
     * - for post-JellyBean devices, where supported, [WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER];
     * - when the device is pre-JellyBean, or if the device does not support
     * [WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER], returns
     * [WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER];
     * - if both intents are unsupported, returns null.
     */
    @SuppressLint("InlinedApi")
    fun provideActionIntent(): Intent? = if (apiLevelProvider.provideSdkInt() >= Build.VERSION_CODES.JELLY_BEAN) {
        provideActionIntentJellyBean()
    } else {
        provideActionIntentWallpaperChooserIfSupported()
    }

    fun isWallaperChooserAction(intent: Intent?) =
            intent?.action == WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun provideActionIntentJellyBean(): Intent? {
        var intent = provideActionIntentChangeLiveWallpaperIfSupported()
        if (intent == null) {
            intent = provideActionIntentWallpaperChooserIfSupported()
        }
        return intent
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun provideActionIntentChangeLiveWallpaperIfSupported(): Intent? {
        val intent = provideIntentChangeLiveWallpaper()
        val intentActivities = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY)
        return if (intentActivities.isNotEmpty()) intent else null
    }

    private fun provideActionIntentWallpaperChooserIfSupported(): Intent? {
        val intent = provideIntentWallpaperChooser()
        val intentActivities = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY)
        return if (intentActivities.isNotEmpty()) intent else null
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @VisibleForTesting
    fun provideIntentChangeLiveWallpaper() =
            Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra("SET_LOCKSCREEN_WALLPAPER", true)
                putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(packageName, WallpaperServiceImpl::class.java.canonicalName!!)
                )
            }

    @VisibleForTesting
    fun provideIntentWallpaperChooser() =
            Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
}
