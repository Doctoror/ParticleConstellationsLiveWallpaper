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
package com.doctoror.particleswallpaper.engine.configurator

import androidx.annotation.VisibleForTesting
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

/**
 * Monitors for [SceneSettings] changes and configures [SceneConfiguration] based on the settings.
 *
 * Not thread safe!
 */
class SceneConfigurator(
    private val schedulers: SchedulersProvider
) {

    private val densityMultiplier = BehaviorSubject.createDefault(1f)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var disposables: CompositeDisposable? = null

    /**
     * Since wallpaper can take more than single screen width, density should be multiplied based on
     * difference between single screen width and desired wallpaper width.
     */
    fun setDensityMultiplier(multiplier: Float) {
        densityMultiplier.onNext(multiplier)
    }

    fun subscribe(
        configuration: SceneConfiguration,
        configurationLock: Any,
        controller: SceneController,
        settings: SceneSettings,
        observeScheduler: Scheduler
    ) {
        val d = CompositeDisposable()

        disposables?.dispose()
        disposables = d

        d.add(
            Observable
                .combineLatest(
                    settings.observeDensity(),
                    densityMultiplier.distinctUntilChanged(),
                    BiFunction<Int, Float, Int> { density, multiplier ->
                        (density * multiplier).toInt()
                    }
                )
                .subscribeOn(schedulers.io())
                .observeOn(observeScheduler)
                .subscribe { v ->
                    synchronized(configurationLock) {
                        configuration.density = v
                        controller.makeFreshFrame()
                    }
                }
        )

        d.add(
            settings
                .observeFrameDelay()
                .subscribeOn(schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(configuration::setFrameDelay)
        )

        d.add(
            settings
                .observeLineLength()
                .subscribeOn(schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(configuration::setLineLength)
        )

        d.add(
            settings
                .observeLineScale()
                .subscribeOn(schedulers.io())
                .observeOn(observeScheduler)
                .subscribe { v ->
                    synchronized(configurationLock) {
                        configuration.lineThickness = v
                        controller.makeFreshFrame()
                    }
                }
        )

        d.add(
            settings.observeParticleColor()
                .subscribeOn(schedulers.io())
                .observeOn(observeScheduler)
                .subscribe { c ->
                    configuration.particleColor = c
                    configuration.lineColor = c
                }
        )

        d.add(
            settings
                .observeParticleScale()
                .subscribeOn(schedulers.io())
                .observeOn(observeScheduler)
                .subscribe { v ->
                    synchronized(configurationLock) {
                        val radiusRange = ParticleRadiusMapper.transform(v)
                        configuration.setParticleRadiusRange(radiusRange.first, radiusRange.second)
                        controller.makeFreshFrame()
                    }
                }
        )

        d.add(
            settings
                .observeSpeedFactor()
                .subscribeOn(schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(configuration::setSpeedFactor)
        )
    }

    fun dispose() {
        disposables?.dispose()
        disposables = null
    }
}
