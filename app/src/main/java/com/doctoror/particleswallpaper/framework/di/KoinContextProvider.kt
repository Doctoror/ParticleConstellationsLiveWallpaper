/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.framework.di

import android.content.Context
import com.doctoror.particleswallpaper.BuildConfig
import org.koin.core.KoinContext
import org.koin.standalone.StandAloneContext

/**
 * To avoid crashes when Koin is not getting initialized or getting initialized multiple times, do
 * not rely on Application to initialize it. Is a solution to:
 *
 * https://github.com/InsertKoinIO/koin/issues/286
 */
object KoinContextProvider {

    private var koinStarted = false

    @Synchronized
    fun getKoinContext(context: Context) = if (BuildConfig.DEBUG) {
        getKoinContextDebug(context)
    } else {
        getKoinContextRelease(context)
    }

    /**
     * Tests can call startKoin on their own, so must check real value of StandAloneContext state.
     */
    private fun getKoinContextDebug(context: Context): KoinContext {
        val isStarted = StandAloneContext::class.java.getDeclaredField("isStarted")
            .apply { isAccessible = true }
            .get(StandAloneContext) as Boolean

        koinStarted = isStarted

        if (!isStarted) {
            koinStarted = true
            KoinStarter().startKoin(context.applicationContext)
        }

        return StandAloneContext.koinContext as KoinContext
    }

    private fun getKoinContextRelease(context: Context): KoinContext {
        if (!koinStarted) {
            koinStarted = true
            KoinStarter().startKoin(context.applicationContext)
        }
        return StandAloneContext.koinContext as KoinContext
    }
}
