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
package com.doctoror.particleswallpaper.presentation.config

import android.annotation.TargetApi
import android.app.ActionBar
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.interactor.OpenChangeWallpaperIntentUseCase
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.actions.ActivityStartActivityForResultAction

/**
 * Created by Yaroslav Mytkalyk on 17.06.17.
 *
 * Lollipop version of [ConfigActivityPresenter]. Shows [Toolbar] [ActionBar] with "preview" action.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ConfigActivityPresenterLollipop(
        schedulers: SchedulersProvider,
        configurator: SceneConfigurator,
        settings: SettingsRepository)
    : ConfigActivityPresenter(schedulers, configurator, settings) {

    override fun onTakeView(view: ConfigActivityView) {
        super.onTakeView(view)
        initToolbar(view)
    }

    private fun initToolbar(view: ConfigActivityView) {
        val activity = view.getActivity()
        val root = activity.findViewById<ViewGroup>(R.id.toolbarContainer)!!
        val toolbar = activity.layoutInflater
                .inflate(R.layout.activity_config_toolbar, root, false) as Toolbar
        root.addView(toolbar, 0)
        activity.setActionBar(toolbar)
        activity.actionBar?.displayOptions =
                ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_HOME
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        view.getActivity().menuInflater.inflate(R.menu.activity_config, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                view.getActivity().finish()
                return true
            }

            R.id.actionPreview -> {
                val activity = view.getActivity()
                OpenChangeWallpaperIntentUseCase(
                        activity.packageName,
                        ActivityStartActivityForResultAction(activity))
                        .useCase().subscribe { started -> if (!started) onPreviewStartFailed() }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onPreviewStartFailed() {
        Toast.makeText(view.getActivity(), R.string.Failed_to_start_preview, Toast.LENGTH_LONG)
                .show()
    }
}
