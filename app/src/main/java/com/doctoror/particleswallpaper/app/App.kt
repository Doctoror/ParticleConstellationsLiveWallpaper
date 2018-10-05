package com.doctoror.particleswallpaper.app

import android.app.Application
import android.os.Looper
import android.os.StrictMode
import com.doctoror.particleswallpaper.BuildConfig
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * The application instance
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initStrictMode()
        initAsyncScheduler()
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

    private fun initAsyncScheduler() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            AndroidSchedulers.from(Looper.getMainLooper(), true)
        }
    }
}
