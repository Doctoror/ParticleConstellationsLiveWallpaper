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
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.lifecycle.LifecycleActivity
import com.doctoror.particleswallpaper.framework.view.removeOnGlobalLayoutListenerCompat
import org.koin.android.ext.android.get

class ConfigActivity : LifecycleActivity(), ConfigActivityView {

    private var fragmentTransactionsAllowed = false

    private var particlesView: Animatable? = null

    private lateinit var presenter: ConfigActivityPresenter

    private val view = ConfigActivityViewFactory().newView(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentTransactionsAllowed = true

        setContentView(R.layout.activity_config)

        val particlesView = findViewById<View>(R.id.particlesView)
        this.particlesView = particlesView as Animatable
        view.particlesView = particlesView

        presenter = get(parameters = {
            ConfigActivityModuleProvider.createArguments(
                this,
                particlesView as SceneConfiguration,
                particlesView as SceneController
            )
        })

        particlesView.viewTreeObserver?.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    particlesView.viewTreeObserver?.removeOnGlobalLayoutListenerCompat(this)
                    presenter.setDimensions(particlesView.width, particlesView.height)
                }
            })

        lifecycle.addObserver(presenter)
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

    override fun onDestroy() {
        super.onDestroy()
        view.particlesView = null
    }

    override fun displayBackgroundColor(color: Int) {
        view.displayBackgroundColor(color)
    }

    override fun displayBackground(background: Bitmap?) {
        view.displayBackground(background)
    }

    override fun onCreateOptionsMenu(menu: Menu) = presenter.onCreateOptionsMenu(menu)

    override fun onOptionsItemSelected(item: MenuItem) = presenter.onOptionsItemSelected(item)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode)
    }
}
