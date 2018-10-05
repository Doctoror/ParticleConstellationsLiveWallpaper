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
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.doctoror.particleswallpaper.engine.WallpaperServiceImpl
import com.doctoror.particleswallpaper.config.app.ApiLevelProvider
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class OpenChangeWallpaperIntentProviderTest {

    private val apiLevelProvider: ApiLevelProvider = mock()
    private val packageName = "packageName"
    private val packageManager: PackageManager = mock()

    private val underTest = OpenChangeWallpaperIntentProvider(
            apiLevelProvider, packageManager, packageName
    )

    private fun givenSdkIsJellyBean() {
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.JELLY_BEAN)
    }

    private fun givenSdkIsIcsMr1() {
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    }

    private fun givenIntentSupported(intent: Intent) {
        whenever(packageManager.queryIntentActivities(argWhere { it.action == intent.action }, any()))
                .thenReturn(listOf(mock()))
    }

    @Before
    fun setup() {
        givenSdkIsJellyBean()
    }

    @Test
    fun providesActionChangeLiveWallpaperIntentForJellyBeanAndLaterIfSupported() {
        // Given
        givenSdkIsJellyBean()
        givenIntentSupported(underTest.provideIntentChangeLiveWallpaper())

        // When
        val result = underTest.provideActionIntent()!!

        assertEquals(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER, result.action)
        assertTrue(result.getBooleanExtra("SET_LOCKSCREEN_WALLPAPER", false))
        assertEquals(
                ComponentName(packageName, WallpaperServiceImpl::class.java.canonicalName),
                result.getParcelableExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT))
    }

    @Test
    fun providesActionLiveWallpaperChooserForJellyBeanIfSupported() {
        // Given
        givenSdkIsJellyBean()
        givenIntentSupported(underTest.provideIntentWallpaperChooser())

        // When
        val result = underTest.provideActionIntent()!!

        assertEquals(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER, result.action)
    }

    @Test
    fun providesActionLiveWallpaperChooserForIcsMr1IfSupported() {
        // Given
        givenSdkIsIcsMr1()
        givenIntentSupported(underTest.provideIntentWallpaperChooser())

        // When
        val result = underTest.provideActionIntent()!!

        // Then
        assertEquals(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER, result.action)
    }

    @Test
    fun providesNullWhenNothingSupportedForJellyBean() {
        // Given
        givenSdkIsJellyBean()

        // When
        val result = underTest.provideActionIntent()

        // Then
        assertNull(result)
    }

    @Test
    fun providesNullWhenNothingSupportedForIcsMr1() {
        // Given
        givenSdkIsIcsMr1()

        // When
        val result = underTest.provideActionIntent()

        // Then
        assertNull(result)
    }

    @Test
    fun isWallpaperChooserActionTrueForWallpaperChooser() {
        // When
        val result = underTest.isWallaperChooserAction(
                Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))

        // Then
        assertTrue(result)
    }

    @Test
    fun isWallpaperChooserActionFalseForNotWallpaperChooser() {
        // When
        val result = underTest.isWallaperChooserAction(
                Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER))

        // Then
        assertFalse(result)
    }
}
