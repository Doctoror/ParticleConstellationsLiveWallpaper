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
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import com.doctoror.particleswallpaper.engine.canvas.CanvasWallpaperServiceImpl
import com.doctoror.particleswallpaper.engine.opengl.GlWallpaperServiceImpl
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings

class OpenChangeWallpaperIntentProvider(
    private val apiLevelProvider: ApiLevelProvider,
    private val deviceSettings: DeviceSettings,
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
    fun provideActionIntent(): Intent? {
        var intent = provideActionIntentChangeLiveWallpaperIfSupported()
        if (intent == null) {
            intent = provideActionIntentWallpaperChooserIfSupported()
        }
        return intent
    }

    fun isWallaperChooserAction(intent: Intent?) =
        intent?.action == WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER

    private fun provideActionIntentChangeLiveWallpaperIfSupported(): Intent? {
        val intent = provideIntentChangeLiveWallpaper()
        val intentActivities = queryIntentActivities(intent)
        return if (intentActivities.isNotEmpty()) intent else null
    }

    private fun provideActionIntentWallpaperChooserIfSupported(): Intent? {
        val intent = provideIntentWallpaperChooser()
        val intentActivities = queryIntentActivities(intent)
        return if (intentActivities.isNotEmpty()) intent else null
    }

    @VisibleForTesting
    fun provideIntentChangeLiveWallpaper() =
        Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra("SET_LOCKSCREEN_WALLPAPER", true)
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(
                    packageName,
                    if (deviceSettings.openglEnabled)
                        GlWallpaperServiceImpl::class.java.canonicalName!!
                    else
                        CanvasWallpaperServiceImpl::class.java.canonicalName!!

                )
            )
        }

    @VisibleForTesting
    fun provideIntentWallpaperChooser() =
        Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)

    private fun queryIntentActivities(intent: Intent) =
        if (apiLevelProvider.provideSdkInt() >= Build.VERSION_CODES.TIRAMISU) {
            queryIntentActivities33(intent)
        } else {
            queryIntentActivitiesLegacy(intent)
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun queryIntentActivities33(intent: Intent) = packageManager.queryIntentActivities(
        intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
    )

    @Suppress("DEPRECATION")
    private fun queryIntentActivitiesLegacy(intent: Intent) = packageManager.queryIntentActivities(
        intent, PackageManager.MATCH_DEFAULT_ONLY
    )
}
