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
package com.doctoror.particleswallpaper.app

import android.app.Activity
import android.app.Fragment
import android.app.Service
import android.content.Context
import com.doctoror.particleswallpaper.framework.di.components.AppComponentProvider
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

/**
 * Based on https://stackoverflow.com/a/46925736/1366471
 */
class ApplicationlessInjection private constructor(context: Context) {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    init {
        AppComponentProvider.provideAppComponent(context).inject(this)
    }

    companion object {

        private var instance: ApplicationlessInjection? = null

        fun getInstance(applicationContext: Context): ApplicationlessInjection {
            var localInstance = instance
            if (localInstance == null) {
                synchronized(ApplicationlessInjection::class.java) {
                    localInstance = instance
                    if (localInstance == null) {
                        localInstance = ApplicationlessInjection(applicationContext)
                        instance = localInstance
                    }
                }
            }
            return localInstance!!
        }
    }
}
