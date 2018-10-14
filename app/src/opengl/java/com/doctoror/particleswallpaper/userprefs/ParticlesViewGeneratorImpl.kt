/*
 * Copyright (C) 2018 Yaroslav Mytkalyk
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

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.doctoror.particlesdrawable.opengl.GlParticlesView
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.util.MultisamplingSupportDetector
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

/**
 * Recreates [GlParticlesView] according to multisampling preferences changes.
 */
class ParticlesViewGeneratorImpl(
    private val context: Context,
    private val multisamplingSupportDetector: MultisamplingSupportDetector,
    private val openGlSettings: OpenGlSettings,
    private val schedulersProvider: SchedulersProvider
) : ParticlesViewGenerator {

    private val viewInstanceSubject = PublishSubject.create<View>()

    private var disposable: Disposable? = null

    private var lastNumSamples = -1

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        disposable = openGlSettings
            .observeNumSamples()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.mainThread())
            .subscribe(this::onNumSamplesLoaded)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        disposable?.dispose()
        disposable = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        lastNumSamples = -1
    }

    override fun observeParticlesViewInstance() = viewInstanceSubject

    private fun onNumSamplesLoaded(numSamples: Int) {
        if (lastNumSamples != numSamples) {
            lastNumSamples = numSamples

            val instance = makeInstance(context, numSamples)

            multisamplingSupportDetector.writeMultisamplingSupportStatus(
                numSamples, instance.chosenNumSamples
            )

            viewInstanceSubject.onNext(instance)
        }
    }

    private fun makeInstance(
        context: Context,
        numSamples: Int
    ) = GlParticlesView(context, numSamples)
}
