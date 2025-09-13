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
package com.doctoror.particleswallpaper.userprefs.preview

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.doctoror.particleswallpaper.engine.canvas.CanvasWallpaperServiceImpl
import com.doctoror.particleswallpaper.engine.opengl.GlWallpaperServiceImpl
import com.doctoror.particleswallpaper.framework.app.ApiLevelProvider
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.kotlin.any
import org.mockito.kotlin.argWhere
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenChangeWallpaperIntentProviderTest {

    private val apiLevelProvider: ApiLevelProvider = mock()
    private val deviceSettings: DeviceSettings = mock {
        on(it.openglEnabled).thenReturn(true)
    }
    private val packageName = "packageName"
    private val packageManager: PackageManager = mock()

    private val underTest = OpenChangeWallpaperIntentProvider(
        apiLevelProvider, deviceSettings, packageManager, packageName
    )

    private fun givenSdkIs33() {
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.JELLY_BEAN)
    }

    private fun givenSdkIs19() {
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.KITKAT)
    }

    private fun givenIntentSupported(intent: Intent) {
        whenever(
            packageManager.queryIntentActivities(
                argWhere { it.action == intent.action },
                any<PackageManager.ResolveInfoFlags>()
            )
        ).thenReturn(listOf(mock()))

        @Suppress("DEPRECATION")
        whenever(
            packageManager.queryIntentActivities(
                argWhere { it.action == intent.action },
                any<Int>()
            )
        ).thenReturn(listOf(mock()))
    }

    @Before
    fun setup() {
        givenSdkIs33()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun providesActionForOpenglApi33IfSupported() {
        // Given
        givenSdkIs33()
        givenIntentSupported(underTest.provideIntentChangeLiveWallpaper())

        // When
        val result = underTest.provideActionIntent()!!

        assertEquals(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER, result.action)
        assertTrue(result.getBooleanExtra("SET_LOCKSCREEN_WALLPAPER", false))
        assertEquals(
            ComponentName(packageName, GlWallpaperServiceImpl::class.java.canonicalName!!),
            result.getParcelableExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName::class.java
            )
        )
    }

    @Test
    fun providesActionForCanvasApi33IfSupported() {
        // Given
        givenSdkIs33()
        givenIntentSupported(underTest.provideIntentChangeLiveWallpaper())
        whenever(deviceSettings.openglEnabled).thenReturn(false)

        // When
        val result = underTest.provideActionIntent()!!

        assertEquals(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER, result.action)
        assertTrue(result.getBooleanExtra("SET_LOCKSCREEN_WALLPAPER", false))
        assertEquals(
            ComponentName(packageName, CanvasWallpaperServiceImpl::class.java.canonicalName!!),
            result.getParcelableExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName::class.java
            )
        )
    }

    @Test
    fun providesActionLiveWallpaperChooserForApi33IfSupported() {
        // Given
        givenSdkIs33()
        givenIntentSupported(underTest.provideIntentWallpaperChooser())

        // When
        val result = underTest.provideActionIntent()!!

        assertEquals(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER, result.action)
    }

    @Test
    fun providesActionLiveWallpaperChooserForApi19IfSupported() {
        // Given
        givenSdkIs19()
        givenIntentSupported(underTest.provideIntentWallpaperChooser())

        // When
        val result = underTest.provideActionIntent()!!

        // Then
        assertEquals(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER, result.action)
    }

    @Test
    fun providesNullWhenNothingSupportedForApi33() {
        // Given
        givenSdkIs33()

        // When
        val result = underTest.provideActionIntent()

        // Then
        assertNull(result)
    }

    @Test
    fun providesNullWhenNothingSupportedForApi19() {
        // Given
        givenSdkIs19()

        // When
        val result = underTest.provideActionIntent()

        // Then
        assertNull(result)
    }

    @Test
    fun isWallpaperChooserActionTrueForWallpaperChooser() {
        // When
        val result = underTest.isWallaperChooserAction(
            Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
        )

        // Then
        assertTrue(result)
    }

    @Test
    fun isWallpaperChooserActionFalseForNotWallpaperChooser() {
        // When
        val result = underTest.isWallaperChooserAction(
            Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        )

        // Then
        assertFalse(result)
    }
}
