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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.doctoror.particlesdrawable.ParticlesView
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.presentation.ApplicationlessInjection
import com.doctoror.particleswallpaper.presentation.base.LifecycleActivity
import javax.inject.Inject

class ConfigActivity : LifecycleActivity(), ConfigActivityView {

    @Inject
    lateinit var presenter: ConfigActivityPresenter

    private var fragmentTransactionsAllowed = false

    private var view: ParticlesView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ApplicationlessInjection
                .getInstance(applicationContext)
                .activityInjector
                .inject(this)
        super.onCreate(savedInstanceState)
        fragmentTransactionsAllowed = true

        setContentView(R.layout.activity_config)
        lifecycle.addObserver(presenter)

        view = findViewById(R.id.particlesView)
        presenter.configuration = view
        presenter.controller = view
    }

    override fun onStart() {
        super.onStart()
        view?.start()
        fragmentTransactionsAllowed = true
    }

    override fun onResume() {
        super.onResume()
        fragmentTransactionsAllowed = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragmentTransactionsAllowed = false
    }

    override fun onStop() {
        super.onStop()
        view?.stop()
    }

    override fun getBackgroundView() = findViewById<ImageView>(R.id.bg)!!

    override fun onCreateOptionsMenu(menu: Menu) = presenter.onCreateOptionsMenu(menu)

    override fun onOptionsItemSelected(item: MenuItem) = presenter.onOptionsItemSelected(item)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode)
    }
}
