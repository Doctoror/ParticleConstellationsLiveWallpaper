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
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.doctoror.particlesdrawable.ParticlesView
import com.doctoror.particlesdrawable.opengl.GlParticlesView
import com.doctoror.particlesdrawable.opengl.chooser.EGLConfigChooserCallback
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.util.MultisamplingConfigSpecParser
import com.doctoror.particleswallpaper.framework.util.MultisamplingSupportDetector
import com.doctoror.particleswallpaper.framework.view.GlErrorHandlingParticlesView
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * Recreates [GlParticlesView] pr [ParticlesView] according to multisampling preferences changes or
 * opengl enabled changes.
 */
class ParticlesViewGenerator(
    private val context: Context,
    private val deviceSettings: DeviceSettings,
    private val multisamplingConfigSpecParser: MultisamplingConfigSpecParser,
    private val multisamplingSupportDetector: MultisamplingSupportDetector,
    private val openGlSettings: OpenGlSettings,
    private val schedulersProvider: SchedulersProvider
) : LifecycleObserver {

    private val viewInstanceSubject = PublishSubject.create<View>()

    private var disposable: Disposable? = null

    private var lastOpenglEnabled = true
    private var lastNumSamples = -1

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        disposable = Observable
            .combineLatest(
                deviceSettings.observeOpenglEnabled(),
                openGlSettings.observeNumSamples(),
                BiFunction<Boolean, Int, Pair<Boolean, Int>> { enabled, values ->
                    enabled to values
                }
            )
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.mainThread())
            .subscribe { (enabled: Boolean, samples: Int) ->
                onSettingsLoaded(enabled, samples)
            }
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

    fun observeParticlesViewInstance(): Observable<View> = viewInstanceSubject

    private fun onSettingsLoaded(
        openglEnabled: Boolean,
        numSamples: Int
    ) {
        if (lastOpenglEnabled != openglEnabled || lastNumSamples != numSamples) {
            lastOpenglEnabled = openglEnabled
            lastNumSamples = numSamples
            viewInstanceSubject.onNext(makeInstance(context, openglEnabled, numSamples))
        }
    }

    private fun makeInstance(
        context: Context,
        openglEnabled: Boolean,
        numSamples: Int
    ): View = if (openglEnabled) {
        GlErrorHandlingParticlesView(
            context,
            numSamples,
            EGLConfigChooserCallbackImpl(
                multisamplingConfigSpecParser,
                multisamplingSupportDetector,
                numSamples
            )
        )
    } else {
        ParticlesView(context)
    }

    private class EGLConfigChooserCallbackImpl(
        private val multisamplingConfigSpecParser: MultisamplingConfigSpecParser,
        private val multisamplingSupportDetector: MultisamplingSupportDetector,
        private val requestedNumSamples: Int
    ) : EGLConfigChooserCallback {

        override fun onConfigChosen(config: IntArray?) {
            multisamplingSupportDetector.writeMultisamplingSupportStatus(
                requestedNumSamples,
                multisamplingConfigSpecParser.extractNumSamplesFromConfigSpec(config)
            )
        }
    }
}
