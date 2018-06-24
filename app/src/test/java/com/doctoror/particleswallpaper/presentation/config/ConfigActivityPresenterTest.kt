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
package com.doctoror.particleswallpaper.presentation.config

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.presentation.util.ThemeUtils
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ConfigActivityPresenterTest {

    private val activity: Activity = mock()
    private val configurator: SceneConfigurator = mock()
    private val settings: SettingsRepository = mock()
    private val requestManager = spy(Glide.with(RuntimeEnvironment.application))
    private val view: ConfigActivityView = mock()

    private val underTest = ConfigActivityPresenter(
            activity, TrampolineSchedulers(), configurator, requestManager, settings, view)

    @Before
    fun setup() {
        underTest.configuration = mock()
        underTest.controller = mock()
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
        val target: ImageView = mock {
            on(it.context).doReturn(RuntimeEnvironment.application)
        }
        whenever(view.getBackgroundView()).thenReturn(target)

        val color = Color.CYAN
        whenever(settings.getBackgroundColor()).thenReturn(Observable.just(color))
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(NO_URI))

        // When
        underTest.onStart()

        // Then
        verify(target).setImageDrawable(null)

        val captor = argumentCaptor<Drawable>()
        verify(target).background = captor.capture()

        assertTrue(captor.firstValue is ColorDrawable)
        assertEquals(color, (captor.firstValue as ColorDrawable).color)
    }

    @Test
    fun clearsBackgroundWhenLoadedColorIsWindowBackground() {
        // Given
        val target: ImageView = mock {
            on(it.context).doReturn(RuntimeEnvironment.application)
        }
        whenever(view.getBackgroundView()).thenReturn(target)

        whenever(settings.getBackgroundColor()).thenReturn(Observable.just(ThemeUtils.getColor(
                RuntimeEnvironment.application.theme, android.R.attr.windowBackground)))

        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(NO_URI))

        // When
        underTest.onStart()

        // Then
        verify(target).setImageDrawable(null)
        verify(target).background = null
    }

    @Test
    fun doesNotLoadAndRegistersOnGlobalLayoutWhenUriNotBlankButDimensionsNotSet() {
        // Given
        val viewTreeObserver: ViewTreeObserver = mock()

        val target: ImageView = mock {
            on(it.context).doReturn(RuntimeEnvironment.application)
            on(it.viewTreeObserver).doReturn(viewTreeObserver)
        }
        whenever(view.getBackgroundView()).thenReturn(target)

        whenever(settings.getBackgroundColor()).thenReturn(Observable.just(Color.TRANSPARENT))
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just("uri"))

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
            on(it.context).doReturn(RuntimeEnvironment.application)
            on(it.width).doReturn(1)
            on(it.height).doReturn(1)
        }
        whenever(view.getBackgroundView()).thenReturn(target)

        val uri = "uri"
        whenever(settings.getBackgroundColor()).thenReturn(Observable.just(Color.TRANSPARENT))
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(uri))

        // When
        underTest.onTakeView(view)
        underTest.onStart()

        // Then
        verify(requestManager).load(uri)
    }

    private fun mockBackgroundView(): ImageView = mock {
        on(it.context).doReturn(RuntimeEnvironment.application)
    }

    private fun givenBackgroundSourcesMocked() {
        val backgroundView = mockBackgroundView()
        whenever(view.getBackgroundView()).thenReturn(backgroundView)
        whenever(settings.getBackgroundColor()).thenReturn(Observable.just(Color.TRANSPARENT))
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(NO_URI))
    }
}
