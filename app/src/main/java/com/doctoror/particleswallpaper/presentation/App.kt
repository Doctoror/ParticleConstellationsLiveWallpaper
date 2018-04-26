package com.doctoror.particleswallpaper.presentation

import android.os.StrictMode
import com.doctoror.particleswallpaper.BuildConfig
import com.doctoror.particleswallpaper.presentation.di.components.AppComponentProvider
import dagger.android.DaggerApplication

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * The application instance
 */
class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        initStrictMode()
    }

    private fun initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())

            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
        }
    }

    override fun applicationInjector() = AppComponentProvider.provideAppComponent(this)
}
