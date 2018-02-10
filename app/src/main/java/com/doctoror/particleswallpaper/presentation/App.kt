package com.doctoror.particleswallpaper.presentation

import android.app.Application
import android.os.StrictMode
import com.doctoror.particleswallpaper.BuildConfig

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * The application instance
 */
class App: Application() {

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
}
