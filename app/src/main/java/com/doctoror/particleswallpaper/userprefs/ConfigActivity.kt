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
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.lifecycle.Lifecycle
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.framework.di.get
import com.doctoror.particleswallpaper.framework.lifecycle.LifecycleActivity
import com.doctoror.particleswallpaper.framework.view.ViewDimensionsProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class ConfigActivity : LifecycleActivity(), ConfigActivityMenuView {

    private val disposables = CompositeDisposable()

    private var fragmentTransactionsAllowed = false

    private var presenter: ConfigActivityPresenter? = null

    private lateinit var menuPresenter: ConfigActivityMenuPresenter
    private lateinit var view: SceneBackgroundView

    private lateinit var viewContainer: ViewGroup
    private lateinit var viewDimensionsProvider: ViewDimensionsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentTransactionsAllowed = true

        menuPresenter = get(
            context = this,
            parameters = { makeInjectArgumentsForConfigActiivtyMenuPresenter(this) }
        )

        view = get(
            context = this,
            parameters = { makeInjectArgumentsForSceneBackgroundView(this) }
        )

        setContentView(R.layout.activity_config)

        viewContainer = findViewById(R.id.viewContainer)
        viewDimensionsProvider = ViewDimensionsProvider(viewContainer)

        val viewGenerator: ParticlesViewGenerator = get(
            context = this,
            parameters = { makeInjectArgumentsForSceneBackgroundView(this) }
        )

        disposables.add(
            viewGenerator
                .observeParticlesViewInstance()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onParticlesViewReady)
        )

        lifecycle.addObserver(menuPresenter)
        lifecycle.addObserver(viewGenerator)
    }

    private fun onParticlesViewReady(particlesView: View) {
        removeCurrentParticlesView()
        registerParticlesView(particlesView)
        disposeCurrentPresenter()
        injectAndRegisterNewPresenter(particlesView)
    }

    private fun removeCurrentParticlesView() {
        this.view.particlesView?.let {
            viewContainer.removeView(it)
        }
    }

    private fun registerParticlesView(particlesView: View) {
        viewContainer.addView(particlesView, 0)
        view.particlesView = particlesView

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            (particlesView as Animatable).start()
        }
    }

    private fun disposeCurrentPresenter() {
        presenter?.let {
            it.onStop()
            it.onDestroy()
            lifecycle.removeObserver(it)
        }
    }

    private fun injectAndRegisterNewPresenter(particlesView: View) {
        val presenter: ConfigActivityPresenter = get(
            context = this,
            parameters = {
                makeInjectArgumentsForConfigActivityPresenter(
                    particlesView as SceneConfiguration,
                    particlesView as SceneController,
                    view,
                    viewDimensionsProvider
                )
            }
        )

        lifecycle.addObserver(presenter)

        this.presenter = presenter
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setupToolbar() {
        val root = findViewById<ViewGroup>(R.id.toolbarContainer)!!
        val toolbar = layoutInflater
            .inflate(R.layout.activity_config_toolbar, root, false) as Toolbar
        root.addView(toolbar, 0)
        setActionBar(toolbar)
        actionBar?.displayOptions =
                ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_HOME
    }

    override fun inflateMenu(menu: Menu) {
        menuInflater.inflate(R.menu.activity_config, menu)
    }

    override fun onStart() {
        super.onStart()
        fragmentTransactionsAllowed = true
        (view.particlesView as? Animatable)?.start()
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
        (view.particlesView as? Animatable)?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu) = menuPresenter.onCreateOptionsMenu(menu)

    override fun onOptionsItemSelected(item: MenuItem) = menuPresenter.onOptionsItemSelected(item)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHANGE_WALLPAPER && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }
}
