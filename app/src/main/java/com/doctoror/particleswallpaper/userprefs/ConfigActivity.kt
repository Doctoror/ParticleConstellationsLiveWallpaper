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

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.lifecycle.LifecycleActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ConfigActivity : LifecycleActivity(), ConfigActivityView {

    private var fragmentTransactionsAllowed = false

    private val presenter: ConfigActivityPresenter by inject(
        parameters = { parametersOf(this) }
    )

    private var particlesView: Animatable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentTransactionsAllowed = true

        setContentView(R.layout.activity_config)
        lifecycle.addObserver(presenter)

        val particlesView = findViewById<View>(R.id.particlesView)
        this.particlesView = particlesView as Animatable

        presenter.configuration = particlesView as SceneConfiguration
        presenter.controller = particlesView as SceneController
    }

    override fun onStart() {
        super.onStart()
        particlesView?.start()
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
        particlesView?.stop()
    }

    override fun getBackgroundView() = findViewById<ImageView>(R.id.bg)!!

    override fun onCreateOptionsMenu(menu: Menu) = presenter.onCreateOptionsMenu(menu)

    override fun onOptionsItemSelected(item: MenuItem) = presenter.onOptionsItemSelected(item)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode)
    }
}
