package com.doctoror.particleswallpaper.userprefs

import android.content.Context
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import org.koin.dsl.module.module

class ConfigModuleProvider {

    fun provide() = module {

        factory {
            OpenChangeWallpaperIntentProvider(
                apiLevelProvider = get(),
                packageManager = get<Context>().packageManager,
                packageName = get<Context>().packageName
            )
        }
    }
}
