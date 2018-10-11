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

import android.app.Activity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.engine.EngineBackgroundLoader
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class ConfigActivityPresenter(
    private val activity: Activity,
    private val backgroundLoader: EngineBackgroundLoader,
    private val schedulers: SchedulersProvider,
    private val configurator: SceneConfigurator,
    private val settings: SceneSettings,
    private val view: ConfigActivityView
) : LifecycleObserver {

    private val disposables = CompositeDisposable()

    private var bgDisposable: Disposable? = null

    var configuration: SceneConfiguration? = null

    var controller: SceneController? = null

    fun setDimensions(width: Int, height: Int) {
        backgroundLoader.setDimensions(width, height)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate() {
        backgroundLoader.onCreate()
        bgDisposable = backgroundLoader
            .observeBackground()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe(
                { view.displayBackground(it.value) },
                { Log.e("ConfigActiivtyPresenter", "Failed loading background image", it) }
            )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        val configuration = configuration ?: throw IllegalStateException("configuration not set")
        val controller = controller ?: throw IllegalStateException("controller not set")

        disposables.add(settings
            .observeBackgroundColor()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe { view.displayBackgroundColor(it) })

        configurator.subscribe(
            configuration,
            controller,
            settings,
            schedulers.mainThread()
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        disposables.clear()
        configurator.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        bgDisposable?.dispose()
        bgDisposable = null
        backgroundLoader.onDestroy()
    }

    open fun onCreateOptionsMenu(menu: Menu) = false

    open fun onOptionsItemSelected(item: MenuItem) = false

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CODE_CHANGE_WALLPAPER && resultCode == Activity.RESULT_OK) {
            activity.finish()
        }
    }
}
