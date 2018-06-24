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
package com.doctoror.particleswallpaper.data.config

import android.support.annotation.VisibleForTesting
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.data.mapper.DotRadiusMapper
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Yaroslav Mytkalyk on 29.05.17.
 *
 * Not thread safe!
 */
class SceneConfiguratorImpl : SceneConfigurator {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var disposables: CompositeDisposable? = null

    override fun subscribe(
            configuration: SceneConfiguration,
            controller: SceneController,
            settings: SettingsRepository,
            scheduler: Scheduler) {
        val d = CompositeDisposable()

        disposables?.dispose()
        disposables = d

        d.add(settings.getParticlesColor()
                .observeOn(scheduler)
                .subscribe { c ->
                    configuration.dotColor = c
                    configuration.lineColor = c
                })

        d.add(settings.getNumDots()
                .observeOn(scheduler)
                .subscribe { v ->
                    configuration.numDots = v
                    controller.makeBrandNewFrame()
                })

        d.add(settings.getDotScale()
                .observeOn(scheduler)
                .subscribe { v ->
                    val radiusRange = DotRadiusMapper.transform(v)
                    configuration.setDotRadiusRange(radiusRange.first, radiusRange.second)
                    controller.makeBrandNewFrame()
                })

        d.add(settings.getLineScale()
                .observeOn(scheduler)
                .subscribe { v ->
                    configuration.lineThickness = v
                    controller.makeBrandNewFrame()
                })

        d.add(settings.getLineDistance()
                .observeOn(scheduler)
                .subscribe(configuration::setLineDistance))

        d.add(settings.getStepMultiplier()
                .observeOn(scheduler)
                .subscribe(configuration::setStepMultiplier))

        d.add(settings.getFrameDelay()
                .observeOn(scheduler)
                .subscribe(configuration::setFrameDelay))
    }

    override fun dispose() {
        disposables?.dispose()
        disposables = null
    }
}
