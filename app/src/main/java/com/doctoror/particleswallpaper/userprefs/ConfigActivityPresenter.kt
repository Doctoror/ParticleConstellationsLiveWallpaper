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

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.engine.EngineBackgroundLoader
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.view.ViewDimensionsProvider
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.disposables.CompositeDisposable

class ConfigActivityPresenter(
    private val backgroundLoader: EngineBackgroundLoader,
    private val configurator: SceneConfigurator,
    private val sceneConfiguration: SceneConfiguration,
    private val sceneController: SceneController,
    private val schedulers: SchedulersProvider,
    private val settings: SceneSettings,
    private val view: SceneBackgroundView,
    private val viewDimensionsProvider: ViewDimensionsProvider
) : DefaultLifecycleObserver {

    private val sceneLock = Any()

    private val disposablesCreateDestroy = CompositeDisposable()
    private val disposablesStartStop = CompositeDisposable()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        backgroundLoader.onCreate()
        disposablesCreateDestroy.add(backgroundLoader
            .observeBackground()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe(
                { view.displayBackground(it.value) },
                { Log.e("ConfigActiivtyPresenter", "Failed loading background image", it) }
            )
        )

        disposablesCreateDestroy.add(viewDimensionsProvider
            .provideDimensions()
            .subscribe { dimensions ->
                backgroundLoader.setDimensions(
                    dimensions.width,
                    dimensions.height
                )
            }
        )
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        disposablesStartStop.add(
            settings
                .observeBackgroundColor()
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribe(view::displayBackgroundColor)
        )

        configurator.subscribe(
            sceneConfiguration,
            sceneLock,
            sceneController,
            settings,
            schedulers.mainThread()
        )
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        disposablesStartStop.clear()
        configurator.dispose()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        disposablesCreateDestroy.clear()
        backgroundLoader.onDestroy()
    }
}
