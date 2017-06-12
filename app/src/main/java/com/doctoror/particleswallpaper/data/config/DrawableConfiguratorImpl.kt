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
import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.data.mapper.DotRadiusMapper
import com.doctoror.particleswallpaper.domain.config.DrawableConfigurator
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Yaroslav Mytkalyk on 29.05.17.
 *
 * Not thread safe!
 */
class DrawableConfiguratorImpl : DrawableConfigurator {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var disposables: CompositeDisposable? = null

    override fun subscribe(drawable: ParticlesDrawable, settings: SettingsRepository) {
        val d = CompositeDisposable()

        disposables?.dispose()
        disposables = d

        d.add(settings.getParticlesColor()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ c ->
                    drawable.dotColor = c
                    drawable.lineColor = c
                }))

        d.add(settings.getNumDots()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ v ->
                    drawable.numDots = v
                    drawable.makeBrandNewFrame()
                }))

        d.add(settings.getDotScale()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ v ->
                    val radiusRange = DotRadiusMapper.transform(v)
                    drawable.setDotRadiusRange(radiusRange.first, radiusRange.second)
                    drawable.makeBrandNewFrame()
                }))

        d.add(settings.getLineScale()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ v ->
                    drawable.lineThickness = v
                    drawable.makeBrandNewFrame()
                }))

        d.add(settings.getLineDistance()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ v ->
            drawable.lineDistance = v
        }))

        d.add(settings.getStepMultiplier()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ v ->
            drawable.stepMultiplier = v
        }))

        d.add(settings.getFrameDelay()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ v ->
            drawable.frameDelay = v
        }))
    }

    override fun dispose() {
        disposables?.dispose()
        disposables = null
    }
}