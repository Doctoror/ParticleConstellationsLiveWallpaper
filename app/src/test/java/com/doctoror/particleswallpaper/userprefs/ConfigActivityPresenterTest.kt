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
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.RequestManager
import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConfigActivityPresenterTest {

    private val backgroundView: ImageView = mock {
        on { it.context }.thenReturn(ApplicationProvider.getApplicationContext<Context>())
        on { it.width }.thenReturn(1)
        on { it.height }.thenReturn(1)
    }

    private val activity: Activity = mock()
    private val configurator: SceneConfigurator = mock()
    private val settings: SceneSettings = mock()
    private val requestManager: RequestManager = mock()
    private val themeAttrColorResolver: ThemeAttrColorResolver = mock()
    private val view: ConfigActivityView = mock {
        on { it.getBackgroundView() }.thenReturn(backgroundView)
    }

    private val underTest = ConfigActivityPresenter(
        activity,
        TrampolineSchedulers(),
        configurator,
        requestManager,
        settings,
        view,
        themeAttrColorResolver
    )

    @Before
    fun setup() {
        underTest.configuration = mock()
        underTest.controller = mock()
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun finishesWhenWallpaperSet() {
        // When
        underTest.onActivityResult(REQUEST_CODE_CHANGE_WALLPAPER, Activity.RESULT_OK)

        // Then
        verify(activity).finish()
    }

    @Test
    fun doesNotFinishWhenWallpaperNotSet() {
        // When
        underTest.onActivityResult(REQUEST_CODE_CHANGE_WALLPAPER, Activity.RESULT_CANCELED)

        // Then
        verify(activity, never()).finish()
    }

    @Test
    fun configues() {
        // Given
        givenBackgroundSourcesMocked()

        // When
        underTest.onStart()

        // Then
        verify(configurator).subscribe(any(), any(), eq(settings), eq(Schedulers.trampoline()))
    }

    @Test
    fun appliesBackgroundColorWhenUriIsBlank() {
        // Given
        val color = Color.CYAN
        whenever(settings.observeBackgroundColor()).thenReturn(Observable.just(color))
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(NO_URI))

        // When
        underTest.onStart()

        // Then
        verify(backgroundView).setImageDrawable(null)

        val captor = argumentCaptor<Drawable>()
        verify(backgroundView).background = captor.capture()

        assertTrue(captor.firstValue is ColorDrawable)
        assertEquals(color, (captor.firstValue as ColorDrawable).color)
    }

    @Test
    fun clearsBackgroundWhenLoadedColorIsWindowBackground() {
        // Given
        val windowBackground = Color.WHITE
        whenever(themeAttrColorResolver.getColor(any(), eq(android.R.attr.windowBackground)))
            .thenReturn(windowBackground)
        whenever(settings.observeBackgroundColor()).thenReturn(Observable.just(windowBackground))

        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(NO_URI))

        // When
        underTest.onStart()

        // Then
        verify(backgroundView).setImageDrawable(null)
        verify(backgroundView).background = null
    }

    @Test
    fun doesNotLoadAndRegistersOnGlobalLayoutWhenUriNotBlankButDimensionsNotSet() {
        // Given
        val viewTreeObserver: ViewTreeObserver = mock()
        whenever(backgroundView.viewTreeObserver).thenReturn(viewTreeObserver)
        whenever(backgroundView.width).thenReturn(0)
        whenever(backgroundView.height).thenReturn(0)

        whenever(settings.observeBackgroundColor()).thenReturn(Observable.just(Color.TRANSPARENT))
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just("uri"))

        // When
        underTest.onStart()

        // Then
        verify(requestManager, never()).load(any<Uri>())
        verify(viewTreeObserver).addOnGlobalLayoutListener(any())
    }

    @Test
    fun loadsImageWhenUriNotBlankAndDimensionsSet() {
        // Given
        val target: ImageView = mock {
            on(it.context).doReturn(ApplicationProvider.getApplicationContext<Context>())
            on(it.width).doReturn(1)
            on(it.height).doReturn(1)
        }
        whenever(view.getBackgroundView()).thenReturn(target)

        val uri = "uri"
        whenever(settings.observeBackgroundColor()).thenReturn(Observable.just(Color.TRANSPARENT))
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(uri))

        // When
        underTest.onStart()

        // Then
        verify(requestManager).load(uri)
    }

    private fun givenBackgroundSourcesMocked() {
        whenever(settings.observeBackgroundColor()).thenReturn(Observable.just(Color.TRANSPARENT))
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(NO_URI))
    }
}
