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
package com.doctoror.particleswallpaper.domain.interactor

import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import com.doctoror.particleswallpaper.data.engine.WallpaperServiceImpl
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_CHANGE_WALLPAPER
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class OpenChangeWallpaperIntentUseCaseTest {

    private val action: StartActivityForResultAction = mock()
    private val apiLevelProvider: ApiLevelProvider = mock()
    private val packageName = "packageName"

    private val underTest = OpenChangeWallpaperIntentUseCase(
            packageName, action, apiLevelProvider)

    private fun givenSdkIsJellyBean() {
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.JELLY_BEAN)
    }

    private fun givenSdkIsIcsMr1() {
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    }

    @Before
    fun setup() {
        givenSdkIsJellyBean()
    }

    @Test
    fun opensWallpaperChoserForJellyBeanAndLater() {
        // When
        val o = underTest.useCase().test()

        // Then
        o.assertResult(true)

        val captor = argumentCaptor<Intent>()
        verify(action).startActivityForResult(captor.capture(), eq(REQUEST_CODE_CHANGE_WALLPAPER))

        assertEquals(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER, captor.firstValue.action)
        assertEquals(
                ComponentName(packageName, WallpaperServiceImpl::class.java.canonicalName),
                captor.firstValue.getParcelableExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT))
    }

    @Test
    fun opensWallpaperChoserForIcsMr1() {
        // Given
        givenSdkIsIcsMr1()

        // When
        val o = underTest.useCase().test()

        // Then
        o.assertResult(true)

        val captor = argumentCaptor<Intent>()
        verify(action).startActivityForResult(captor.capture(), eq(REQUEST_CODE_CHANGE_WALLPAPER))

        assertEquals(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER, captor.firstValue.action)
    }

    @Test
    fun returnsFalseOnActivityNotFoundException() {
        // Given
        whenever(action.startActivityForResult(any(), any())).thenThrow(ActivityNotFoundException())

        // When
        val o = underTest.useCase().test()

        // Then
        o.assertNoErrors()
        o.assertResult(false)
    }
}
