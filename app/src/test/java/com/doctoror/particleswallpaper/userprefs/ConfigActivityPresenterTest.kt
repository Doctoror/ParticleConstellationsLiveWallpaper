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

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.engine.EngineBackgroundLoader
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.util.Optional
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
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

    private val view: ConfigActivityView = mock()

    private val underTest = ConfigActivityPresenter(
        backgroundLoader,
        configurator,
        configuration,
        controller,
        schedulers,
        settings,
        view
    )

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun forwardsDimensionsToBackgroundLoader() {
        val w = 1
        val h = 2

        underTest.setDimensions(w, h)

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
        underTest.setDimensions(1, 2)

        verify(view).displayBackground(null)
    }

    @Test
    fun loadsBackground() {
        val bitmap: Bitmap = mock()
        whenever(backgroundLoader.observeBackground())
            .thenReturn(Observable.just(Optional(bitmap)))

        underTest.onCreate()
        underTest.setDimensions(1, 2)

        verify(view).displayBackground(bitmap)
    }

    @Test
    fun loadsBackgroundColor() {
        val color = Color.CYAN
        whenever(settings.observeBackgroundColor())
            .thenReturn(Observable.just(color))

        underTest.onStart()
        underTest.setDimensions(1, 2)

        verify(view).displayBackgroundColor(color)
    }

    @Test
    fun finishesWhenWallpaperSet() {
        // When
        underTest.onActivityResult(REQUEST_CODE_CHANGE_WALLPAPER, Activity.RESULT_OK)

        // Then
        verify(view).finish()
    }

    @Test
    fun doesNotFinishWhenWallpaperNotSet() {
        // When
        underTest.onActivityResult(REQUEST_CODE_CHANGE_WALLPAPER, Activity.RESULT_CANCELED)

        // Then
        verify(view, never()).finish()
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
