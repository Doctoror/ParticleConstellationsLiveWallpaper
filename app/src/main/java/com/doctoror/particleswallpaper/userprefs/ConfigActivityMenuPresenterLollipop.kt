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
package com.doctoror.particleswallpaper.userprefs

import android.annotation.TargetApi
import android.app.ActionBar
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentUseCase

/**
 * Lollipop version of [ConfigActivityMenuPresenter]. Shows [Toolbar] [ActionBar] with "Apply"
 * action.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ConfigActivityMenuPresenterLollipop(
    private val openChangeWallpaperIntentProvider: OpenChangeWallpaperIntentProvider,
    private val openChangeWallpaperIntentUseCase: OpenChangeWallpaperIntentUseCase,
    private val view: ConfigActivityMenuView
) : ConfigActivityMenuPresenter {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        view.setupToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (openChangeWallpaperIntentProvider.provideActionIntent() != null) {
            view.inflateMenu(menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                view.finish()
                return true
            }

            R.id.actionApply -> {
                openChangeWallpaperIntentUseCase.action().subscribe()
            }
        }
        return false
    }
}
