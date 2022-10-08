package com.doctoror.particleswallpaper.userprefs.multisampling

import android.app.WallpaperInfo
import android.app.WallpaperManager
import android.content.Context
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Test

class WallpaperCheckerUseCaseTest {

    private val packageName = "com.doctoror.particleswallpaper"

    private val context: Context = mock {
        on { it.packageName }.doReturn(packageName)
    }

    private val underTest = WallpaperCheckerUseCase(context)

    @Test
    fun returnsFalseWhenWallpaperServiceIsNull() {
        underTest.wallpaperInstalledSource().test().assertResult(false)
    }

    @Test
    fun returnsFalseWhenWallpaperInfoIsNull() {
        mockWallpaperManagerWithWallpaperInfo(null)
        underTest.wallpaperInstalledSource().test().assertResult(false)
    }

    @Test
    fun returnsFalseWhenWallpaperInfoIsPackageIsNull() {
        mockWallpaperManagerWithWallpaperInfo(mockWallpaperInfoWithPackageName(null))
        underTest.wallpaperInstalledSource().test().assertResult(false)
    }

    @Test
    fun returnsFalseWhenWallpaperInfoPackageDiffers() {
        mockWallpaperManagerWithWallpaperInfo(
            mockWallpaperInfoWithPackageName("com.doctoror.particlesdrawable")
        )
        underTest.wallpaperInstalledSource().test().assertResult(false)
    }

    @Test
    fun returnsTrueWhenWallpaperInfoPackageMatches() {
        mockWallpaperManagerWithWallpaperInfo(
            mockWallpaperInfoWithPackageName(packageName)
        )
        underTest.wallpaperInstalledSource().test().assertResult(true)
    }

    private fun mockWallpaperInfoWithPackageName(packageName: String?): WallpaperInfo = mock {
        on { it.packageName }.doReturn(packageName)
    }

    private fun mockWallpaperManagerWithWallpaperInfo(wallpaperInfo: WallpaperInfo?) {
        val wallpaperManager: WallpaperManager = mock {
            on { it.wallpaperInfo }.doReturn(wallpaperInfo)
        }

        whenever(context.getSystemService(Context.WALLPAPER_SERVICE))
            .thenReturn(wallpaperManager)
    }
}
