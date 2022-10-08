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

import android.graphics.Bitmap
import android.graphics.Color
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.engine.EngineBackgroundLoader
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.util.Optional
import com.doctoror.particleswallpaper.framework.view.Dimensions
import com.doctoror.particleswallpaper.framework.view.ViewDimensionsProvider
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConfigActivityPresenterTest {

    private val backgroundLoader: EngineBackgroundLoader = mock {
        on { it.observeBackground() }.thenReturn(Observable.just(Optional<Bitmap>(null)))
    }

    private val configurator: SceneConfigurator = mock()
    private val configuration: SceneConfiguration = mock()
    private val controller: SceneController = mock()

    private val schedulers = TrampolineSchedulers()

    private val settings: SceneSettings = mock {
        whenever(it.observeBackgroundColor()).thenReturn(Observable.just(Color.TRANSPARENT))
        whenever(it.observeBackgroundUri()).thenReturn(Observable.just(NO_URI))
    }

    private val view: SceneBackgroundView = mock()

    private val viewDimensionsProvider: ViewDimensionsProvider = mock {
        on { it.provideDimensions() }.thenReturn(Single.just(Dimensions(1, 1)))
    }

    private val underTest = ConfigActivityPresenter(
        backgroundLoader,
        configurator,
        configuration,
        controller,
        schedulers,
        settings,
        view,
        viewDimensionsProvider
    )

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun forwardsDimensionsToBackgroundLoader() {
        val w = 1
        val h = 2
        whenever(viewDimensionsProvider.provideDimensions())
            .thenReturn(Single.just(Dimensions(w, h)))

        underTest.onCreate()

        verify(backgroundLoader).setDimensions(w, h)
    }

    @Test
    fun forwardsOnCreateToBackgroundLoader() {
        underTest.onCreate()
        verify(backgroundLoader).onCreate()
    }

    @Test
    fun forwardsOnDestroyToBackgroundLoader() {
        underTest.onDestroy()
        verify(backgroundLoader).onDestroy()
    }

    @Test
    fun loadsNullBackground() {
        underTest.onCreate()

        verify(view).displayBackground(null)
    }

    @Test
    fun loadsBackground() {
        val bitmap: Bitmap = mock()
        whenever(backgroundLoader.observeBackground())
            .thenReturn(Observable.just(Optional(bitmap)))

        underTest.onCreate()

        verify(view).displayBackground(bitmap)
    }

    @Test
    fun loadsBackgroundColor() {
        val color = Color.CYAN
        whenever(settings.observeBackgroundColor())
            .thenReturn(Observable.just(color))

        underTest.onStart()

        verify(view).displayBackgroundColor(color)
    }

    @Test
    fun configues() {
        // When
        underTest.onStart()

        // Then
        verify(configurator).subscribe(
            configuration,
            controller,
            settings,
            schedulers.mainThread()
        )
    }
}
