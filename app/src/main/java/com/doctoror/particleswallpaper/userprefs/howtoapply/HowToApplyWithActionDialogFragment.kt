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
package com.doctoror.particleswallpaper.userprefs.howtoapply

import android.app.AlertDialog
import android.app.DialogFragment
import android.os.Bundle
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.app.actions.FragmentStartActivityForResultAction
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentUseCase
import org.koin.android.ext.android.inject

abstract class HowToApplyWithActionDialogFragment : DialogFragment() {

    private val intentProvider: OpenChangeWallpaperIntentProvider by inject()

    protected abstract val message: Int

    override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog
        .Builder(activity!!)
        .setTitle(R.string.How_to_apply)
        .setMessage(message)
        .setPositiveButton(R.string.Apply) { _, _ -> openPreview() }
        .setNegativeButton(R.string.Close, null)
        .create()!!

    private fun openPreview() {
        newOpenChangeWallpaperIntentUseCase()
            .action()
            .subscribe()
    }

    private fun newOpenChangeWallpaperIntentUseCase() =
        OpenChangeWallpaperIntentUseCase(
            intentProvider,
            FragmentStartActivityForResultAction(this)
        )
}
