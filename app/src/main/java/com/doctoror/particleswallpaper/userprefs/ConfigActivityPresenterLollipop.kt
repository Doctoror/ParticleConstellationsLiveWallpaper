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
package com.doctoror.particleswallpaper.userprefs

import android.annotation.TargetApi
import android.app.ActionBar
import android.app.Activity
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toolbar
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.engine.EngineBackgroundLoader
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentUseCase

/**
 * Created by Yaroslav Mytkalyk on 17.06.17.
 *
 * Lollipop version of [ConfigActivityPresenter]. Shows [Toolbar] [ActionBar] with "preview" action.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ConfigActivityPresenterLollipop(
    private val activity: Activity,
    backgroundLoader: EngineBackgroundLoader,
    configurator: SceneConfigurator,
    private val openChangeWallpaperIntentProvider: OpenChangeWallpaperIntentProvider,
    private val openChangeWallpaperIntentUseCase: OpenChangeWallpaperIntentUseCase,
    sceneConfiguration: SceneConfiguration,
    sceneController: SceneController,
    schedulers: SchedulersProvider,
    settings: SceneSettings,
    view: ConfigActivityView
) : ConfigActivityPresenter(
    backgroundLoader,
    configurator,
    sceneConfiguration,
    sceneController,
    schedulers,
    settings,
    view
) {

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        val root = activity.findViewById<ViewGroup>(R.id.toolbarContainer)!!
        val toolbar = activity.layoutInflater
            .inflate(R.layout.activity_config_toolbar, root, false) as Toolbar
        root.addView(toolbar, 0)
        activity.setActionBar(toolbar)
        activity.actionBar?.displayOptions =
                ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_HOME
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (openChangeWallpaperIntentProvider.provideActionIntent() != null) {
            activity.menuInflater.inflate(R.menu.activity_config, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity.finish()
                return true
            }

            R.id.actionApply -> {
                openChangeWallpaperIntentUseCase.action().subscribe()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
