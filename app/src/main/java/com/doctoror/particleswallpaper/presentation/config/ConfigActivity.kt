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

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.presentation.base.LifecycleActivity
import com.doctoror.particleswallpaper.presentation.di.Injector
import com.doctoror.particleswallpaper.presentation.di.components.DaggerActivityComponent
import com.doctoror.particleswallpaper.presentation.di.modules.ActivityModule
import javax.inject.Inject

class ConfigActivity : LifecycleActivity(), ConfigActivityView {

    @Inject lateinit var presenter: ConfigActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerActivityComponent.builder()
                .configComponent(Injector.configComponent)
                .activityModule(ActivityModule())
                .build()
                .inject(this)

        setContentView(R.layout.activity_config)
        presenter.onTakeView(this)
        lifecycle.addObserver(presenter)
    }

    override fun getActivity(): Activity = this

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return presenter.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return presenter.onOptionsItemSelected(item)
    }
}
