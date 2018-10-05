package com.doctoror.particleswallpaper.userprefs.multisampling

import android.app.WallpaperManager
import android.content.Context
import com.doctoror.particleswallpaper.domain.interactor.UseCase
import io.reactivex.Single
import javax.inject.Inject

/**
 * Check if this wallpaper is installed.
 */
class WallpaperCheckerUseCase @Inject constructor(private val context: Context) : UseCase<Boolean> {

    override fun useCase() = Single.fromCallable {
        val wallpaperManager = context.getSystemService(Context.WALLPAPER_SERVICE)
                as WallpaperManager?

        wallpaperManager?.wallpaperInfo?.packageName == context.packageName
    }!!
}
