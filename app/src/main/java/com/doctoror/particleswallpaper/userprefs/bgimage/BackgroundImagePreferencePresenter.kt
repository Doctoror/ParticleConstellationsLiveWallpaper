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
package com.doctoror.particleswallpaper.userprefs.bgimage

import android.app.Activity
import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.app.REQUEST_CODE_PICK_IMAGE
import com.doctoror.particleswallpaper.framework.app.actions.FragmentStartActivityForResultAction
import com.doctoror.particleswallpaper.framework.lifecycle.OnActivityResultCallback
import com.doctoror.particleswallpaper.framework.lifecycle.OnActivityResultCallbackHost
import com.doctoror.particleswallpaper.userprefs.data.DefaultSceneSettings
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings

class BackgroundImagePreferencePresenter(
    private val context: Context,
    private val defaults: DefaultSceneSettings,
    private val glide: Glide,
    private val pickImageUseCase: PickImageUseCase,
    private val releasePersistableUriPermissionUseCase: ReleasePersistableUriPermissionUseCase,
    private val settings: SceneSettings,
    private val takePersistableUriPermissionUseCase: TakePersistableUriPermissionUseCase,
    private val view: BackgroundImagePreferenceView
) {

    private val tag = "BgImagePrefPresenter"

    private val imageHandler: BackgroundImageHandler = BackgroundImageHandlerKitKat()

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
        view.showActionDialog()
    }

    fun clearBackground() {
        imageHandler.clearBackground()
        glide.clearMemory()
    }

    fun pickBackground() {
        imageHandler.pickBackground()
    }

    private val onActivityResultCallback = object : OnActivityResultCallback() {

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                when (requestCode) {
                    REQUEST_CODE_PICK_IMAGE -> {
                        val uri = data.data
                        if (uri == null) {
                            Log.w(tag, "onActivityResult(), data uri is null")
                        } else {
                            glide.clearMemory()
                            imageHandler.onActivityResultAvailable(requestCode, uri)
                        }
                    }
                }
            }
        }
    }

    private interface BackgroundImageHandler {
        fun pickBackground()
        fun clearBackground()
        fun onActivityResultAvailable(requestCode: Int, uri: Uri)
    }

    private inner class BackgroundImageHandlerKitKat : BackgroundImageHandler {

        override fun pickBackground() {
            host?.let {
                try {
                    pickImageUseCase.invoke(context, FragmentStartActivityForResultAction(it))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        R.string.Failed_to_open_image_picker,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun clearBackground() {
            val uri = settings.backgroundUri
            if (uri != NO_URI) {
                releasePersistableUriPermissionUseCase(Uri.parse(uri))
                settings.backgroundUri = defaults.backgroundUri
            }
        }

        override fun onActivityResultAvailable(requestCode: Int, uri: Uri) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                val uriString = uri.toString()
                val previousUri = settings.backgroundUri
                if (uriString != previousUri) {
                    releasePersistableUriPermissionUseCase(Uri.parse(previousUri))
                    takePersistableUriPermissionUseCase.invoke(uri)
                    settings.backgroundUri = uriString
                }
            }
        }
    }
}
