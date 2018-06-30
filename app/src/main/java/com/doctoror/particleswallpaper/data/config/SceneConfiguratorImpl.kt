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
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particleswallpaper.data.mapper.DotRadiusMapper
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Yaroslav Mytkalyk on 29.05.17.
 *
 * Not thread safe!
 */
class SceneConfiguratorImpl(private val schedulers: SchedulersProvider) : SceneConfigurator {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var disposables: CompositeDisposable? = null

    override fun subscribe(scene: ParticlesScene, settings: SettingsRepository) {
        val d = CompositeDisposable()

        disposables?.dispose()
        disposables = d

        d.add(settings.getParticlesColor()
                .observeOn(schedulers.mainThread())
                .subscribe { c ->
                    scene.dotColor = c
                    scene.lineColor = c
                })

        d.add(settings.getNumDots()
                .observeOn(schedulers.mainThread())
                .subscribe { v ->
                    scene.numDots = v
                    scene.makeBrandNewFrame()
                })

        d.add(settings.getDotScale()
                .observeOn(schedulers.mainThread())
                .subscribe { v ->
                    val radiusRange = DotRadiusMapper.transform(v)
                    scene.setDotRadiusRange(radiusRange.first, radiusRange.second)
                    scene.makeBrandNewFrame()
                })

        d.add(settings.getLineScale()
                .observeOn(schedulers.mainThread())
                .subscribe { v ->
                    scene.lineThickness = v
                    scene.makeBrandNewFrame()
                })

        d.add(settings.getLineDistance()
                .observeOn(schedulers.mainThread())
                .subscribe(scene::setLineDistance))

        d.add(settings.getStepMultiplier()
                .observeOn(schedulers.mainThread())
                .subscribe(scene::setStepMultiplier))

        d.add(settings.getFrameDelay()
                .observeOn(schedulers.mainThread())
                .subscribe(scene::setFrameDelay))
    }

    override fun dispose() {
        disposables?.dispose()
        disposables = null
    }
}
