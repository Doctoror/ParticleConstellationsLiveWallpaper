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
package com.doctoror.particleswallpaper.crashreports

import com.crashlytics.android.core.CrashlyticsCore
import com.crashlytics.android.core.PinningInfoProvider

class DisableableCrashlyticsCore : CrashlyticsCore() {

    @Volatile
    var enabled = true

    override fun getPinningInfoProvider(): PinningInfoProvider? {
        return if (enabled) {
            super.getPinningInfoProvider()
        } else {
            null
        }
    }

    override fun log(msg: String?) {
        if (enabled) {
            super.log(msg)
        }
    }

    override fun log(priority: Int, tag: String?, msg: String?) {
        if (enabled) {
            super.log(priority, tag, msg)
        }
    }

    override fun logException(throwable: Throwable?) {
        if (enabled) {
            super.logException(throwable)
        }
    }

    override fun setBool(key: String?, value: Boolean) {
        if (enabled) {
            super.setBool(key, value)
        }
    }

    override fun setDouble(key: String?, value: Double) {
        if (enabled) {
            super.setDouble(key, value)
        }
    }

    override fun setFloat(key: String?, value: Float) {
        if (enabled) {
            super.setFloat(key, value)
        }
    }

    override fun setInt(key: String?, value: Int) {
        if (enabled) {
            super.setInt(key, value)
        }
    }

    override fun setLong(key: String?, value: Long) {
        if (enabled) {
            super.setLong(key, value)
        }
    }

    override fun setString(key: String?, value: String?) {
        if (enabled) {
            super.setString(key, value)
        }
    }

    override fun setUserEmail(email: String?) {
        if (enabled) {
            super.setUserEmail(email)
        }
    }

    override fun setUserIdentifier(identifier: String?) {
        if (enabled) {
            super.setUserIdentifier(identifier)
        }
    }

    override fun setUserName(name: String?) {
        if (enabled) {
            super.setUserName(name)
        }
    }
}
