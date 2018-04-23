package com.doctoror.particleswallpaper.presentation.di

import android.content.Context
import com.doctoror.particleswallpaper.presentation.di.components.DaggerConfigComponent
import com.doctoror.particleswallpaper.presentation.di.modules.AppModule

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * Stores injection components.
 */
class Injector private constructor(appContext: Context) {

    val configComponent = DaggerConfigComponent.builder()
            .appModule(AppModule(appContext))
            .build()!!

    companion object {

        private var instance: Injector? = null

        @Synchronized
        fun getInstance(appContext: Context): Injector {
            val result: Injector
            val instance = instance
            if (instance == null) {
                result = Injector(appContext)
                this.instance = result
            } else {
                result = instance
            }
            return result
        }
    }
}
