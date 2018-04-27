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
package com.doctoror.particleswallpaper.presentation.presenter

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.widget.Toast
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.interactor.OpenChangeWallpaperIntentUseCase
import com.doctoror.particleswallpaper.presentation.actions.FragmentStartActivityForResultAction
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallback
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallbackHost
import com.doctoror.particleswallpaper.presentation.di.scopes.PerPreference
import com.doctoror.particleswallpaper.presentation.view.MvpView

/**
 * Created by Yaroslav Mytkalyk on 03.06.17.
 *
 * Presenter for [com.doctoror.particleswallpaper.presentation.preference.PreviewPreference]
 */
@PerPreference
class PreviewPreferencePresenter(
        private val apiLevelProvider: ApiLevelProvider,
        private val activity: Activity) : Presenter<MvpView> {

    private val requestCodeSetWallpaper = 1

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

    override fun onTakeView(view: MvpView) {
        // Stub
    }

    override fun onStart() {
        // Stub
    }

    override fun onStop() {
        // Stub
    }

    fun onClick() {
        OpenChangeWallpaperIntentUseCase(
                apiLevelProvider,
                activity.packageName,
                FragmentStartActivityForResultAction(
                        host ?: throw IllegalStateException("Host not set")))
                .useCase()
                .subscribe({ v ->
                    if (!v) {
                        Toast.makeText(activity, R.string.Failed_to_start_preview, Toast.LENGTH_LONG)
                                .show()
                    }
                })
    }

    private val onActivityResultCallback = object : OnActivityResultCallback() {

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == requestCodeSetWallpaper && resultCode == Activity.RESULT_OK) {
                activity.finish()
            }
        }
    }
}
