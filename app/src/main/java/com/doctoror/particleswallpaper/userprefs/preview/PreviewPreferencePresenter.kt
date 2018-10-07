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
package com.doctoror.particleswallpaper.userprefs.preview

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.framework.di.scopes.PerPreference
import com.doctoror.particleswallpaper.framework.lifecycle.OnActivityResultCallback
import com.doctoror.particleswallpaper.framework.lifecycle.OnActivityResultCallbackHost

@PerPreference
class PreviewPreferencePresenter(private val activity: Activity) {

    var useCase: OpenChangeWallpaperIntentUseCase? = null

    var host: Fragment? = null
        set(f) {
            val prevHost = host
            if (prevHost !== f) {
                if (prevHost is OnActivityResultCallbackHost) {
                    prevHost.unregsiterCallback(onActivityResultCallback)
                }
                if (f is OnActivityResultCallbackHost) {
                    f.registerCallback(onActivityResultCallback)
                }
                field = f
            }
        }

    fun onClick() {
        useCase!!
            .action()
            .subscribe()
    }

    private val onActivityResultCallback = object : OnActivityResultCallback() {

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == REQUEST_CODE_CHANGE_WALLPAPER && resultCode == Activity.RESULT_OK) {
                activity.finish()
            }
        }
    }
}
