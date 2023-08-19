package com.doctoror.particleswallpaper.userprefs.bgimage

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log

class ReleasePersistableUriPermissionUseCase(private val contentResolver: ContentResolver) {

    operator fun invoke(uri: Uri) {
        val permissions = contentResolver.persistedUriPermissions
        permissions
            .filter { uri == it.uri }
            .forEach {
                try {
                    contentResolver.releasePersistableUriPermission(
                        it.uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Log.w(
                        "ReleasePersistableUriPermissionUseCase",
                        "Failed to release persistable Uri permission",
                        e
                    )
                }
            }
    }
}