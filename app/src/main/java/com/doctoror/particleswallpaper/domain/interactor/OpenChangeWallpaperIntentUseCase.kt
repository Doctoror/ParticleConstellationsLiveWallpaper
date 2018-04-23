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

import android.app.Activity
import android.app.Fragment
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.doctoror.particleswallpaper.data.engine.WallpaperServiceImpl
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_CHANGE_WALLPAPER
import io.reactivex.Single

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 *
 * Opens live wallpaper preview, or wallpaper chooser for pre-Jellybean devices.
 */
class OpenChangeWallpaperIntentUseCase(
        private val activity: Activity? = null,
        private val fragment: Fragment? = null) : UseCase<Boolean> {

    private val tag = "OpenChangeWpUseCase"

    init {
        if ((activity == null && fragment == null) ||
                (activity != null && fragment != null)) {
            throw IllegalArgumentException("Must set either Activity or Fragment")
        }
    }

    override fun useCase() = Single.fromCallable { action() }!!

    private fun action(): Boolean {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val context = getContext() ?: return false
            intent.action = WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context.packageName, WallpaperServiceImpl::class.java.canonicalName))
        } else {
            intent.action = WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER
        }
        return try {
            startActivityForResult(intent, REQUEST_CODE_CHANGE_WALLPAPER)
            true
        } catch (e: ActivityNotFoundException) {
            Log.w(tag, e)
            false
        }
    }

    private fun startActivityForResult(intent: Intent, requestCode: Int) {
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode)
        } else {
            activity?.startActivityForResult(intent, requestCode)
                    ?: throw IllegalStateException("Both Activity and Fragment cannot be null")
        }
    }

    private fun getContext(): Context? {
        return activity ?: fragment!!.activity
    }
}
