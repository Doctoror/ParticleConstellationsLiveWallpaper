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

import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.file.BackgroundImageManager
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallback
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallbackHost
import com.doctoror.particleswallpaper.presentation.di.modules.ConfigModule
import com.doctoror.particleswallpaper.presentation.di.scopes.PerPreference
import com.doctoror.particleswallpaper.presentation.view.BackgroundImagePreferenceView
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Yaroslav Mytkalyk on 03.06.17.
 *
 * Presenter for [BackgroundImagePreferenceView]
 */
@PerPreference
class BackgroundImagePreferencePresenter @Inject constructor(
        val context: Context,
        val settings: MutableSettingsRepository,
        @Named(ConfigModule.DEFAULT) val defaults: SettingsRepository,
        val backgroundImageManager: BackgroundImageManager)
    : Presenter<BackgroundImagePreferenceView> {

    private val tag = "BgImagePrefPresenter"

    private lateinit var view: BackgroundImagePreferenceView

    private val imageHandler: BackgroundImageHandler

    private val requestCodeOpenDocument = 1
    private val requestCodeGetContent = 2

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            imageHandler = BackgroundImageHandlerKitKat()
        } else {
            imageHandler = BackgroundImageHandlerLegacy()
        }
    }

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

    override fun onTakeView(view: BackgroundImagePreferenceView) {
        this.view = view
    }

    override fun onStart() {
        // Stub
    }

    override fun onStop() {
        // Stub
    }

    fun onClick() {
        view.showActionDialog()
    }

    fun clearBackground() {
        imageHandler.clearBackground()
    }

    fun pickBackground() {
        imageHandler.pickBackground()
    }

    val onActivityResultCallback = object : OnActivityResultCallback() {

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                when (requestCode) {
                    requestCodeOpenDocument,
                    requestCodeGetContent -> {
                        val uri = data.data
                        if (uri == null) {
                            Log.w(tag, "onActivityResult(), data uri is null")
                        } else {
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

    private inner open class BackgroundImageHandlerLegacy : BackgroundImageHandler {

        override fun pickBackground() {
            pickByGetContent()
        }

        override fun clearBackground() {
            defaults.getBackgroundUri().take(1).subscribe({
                u ->
                settings.setBackgroundUri(u)
                clearBackgroundFile()
            })
        }

        private fun clearBackgroundFile() {
            Observable.fromCallable({ -> backgroundImageManager.clearBackgroundImage() })
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }

        override fun onActivityResultAvailable(requestCode: Int, uri: Uri) {
            if (requestCode == requestCodeGetContent) {
                handleGetContentUriResult(uri)
            } else {
                handleDefaultUriResult(uri)
            }
        }

        private fun handleGetContentUriResult(uri: Uri) {
            Observable.fromCallable({ -> backgroundImageManager.copyBackgroundToFile(uri) })
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { privateFileUri -> settings.setBackgroundUri(privateFileUri.toString()) },
                            { t ->
                                Log.w(tag, "Failed copying to private file", t)
                                handleDefaultUriResult(uri)
                            })
        }

        private fun handleDefaultUriResult(uri: Uri) {
            settings.setBackgroundUri(uri.toString())
        }

        protected fun pickByGetContent() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                host?.startActivityForResult(
                        Intent.createChooser(intent, null), requestCodeGetContent)
            } catch (e: ActivityNotFoundException) {
                try {
                    host?.startActivityForResult(intent, requestCodeGetContent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, R.string.Failed_to_open_image_picker, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private inner class BackgroundImageHandlerKitKat : BackgroundImageHandlerLegacy() {

        override fun pickBackground() {
            pickByOpenDocument()
        }

        override fun clearBackground() {
            val uriString = settings.getBackgroundUri().blockingFirst()
            if (uriString != NO_URI) {
                val contentResolver = context.contentResolver
                if (contentResolver != null) {
                    val uri = Uri.parse(uriString)
                    val permissions = contentResolver.persistedUriPermissions
                    permissions
                            ?.filter { uri == it.uri }
                            ?.forEach {
                                contentResolver.releasePersistableUriPermission(uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
            }
            super.clearBackground()
        }

        override fun onActivityResultAvailable(requestCode: Int, uri: Uri) {
            if (requestCode == requestCodeOpenDocument) {
                context.contentResolver?.takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            super.onActivityResultAvailable(requestCode, uri)
        }

        private fun pickByOpenDocument() {
            val documentIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            documentIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            documentIntent.type = "image/*"
            try {
                host?.startActivityForResult(documentIntent, requestCodeOpenDocument)
            } catch (e: ActivityNotFoundException) {
                pickByGetContent()
            }
        }
    }
}