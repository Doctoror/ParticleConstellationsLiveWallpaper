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
package com.doctoror.particleswallpaper.userprefs.multisampling

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.app.actions.FragmentStartActivityForResultAction
import com.doctoror.particleswallpaper.framework.di.inject
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentUseCase

class MultisamplingRestartDialog : DialogFragment() {

    private val intentProvider: OpenChangeWallpaperIntentProvider by inject()

    private lateinit var useCase: OpenChangeWallpaperIntentUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        useCase = OpenChangeWallpaperIntentUseCase(
            intentProvider,
            FragmentStartActivityForResultAction(this)
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog
            .Builder(activity)
            .setMessage(R.string.multisampling_restart_message)

        if (canStartPreview()) {
            builder.setPositiveButton(R.string.Set) { _, _ ->
                startPreview()
            }
            builder.setNegativeButton(R.string.Not_now, null)
        } else {
            builder.setPositiveButton(R.string.Close, null)
        }

        return builder.create()
    }

    private fun canStartPreview() = intentProvider.provideActionIntent() != null

    private fun startPreview() {
        useCase.action().subscribe()
    }
}
