package com.doctoror.particleswallpaper.userprefs.bgimage

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log

class TakePersistableUriPermissionUseCase(private val contentResolver: ContentResolver) {

    operator fun invoke(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            Log.w(
                "RequestPersistableUriPermissionUseCase",
                "Failed to take persistable Uri permission for $uri",
                e
            )
        }
    }
}