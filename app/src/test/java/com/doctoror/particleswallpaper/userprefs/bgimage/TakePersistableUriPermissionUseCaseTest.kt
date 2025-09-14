package com.doctoror.particleswallpaper.userprefs.bgimage

import android.app.Application
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class TakePersistableUriPermissionUseCaseTest {

    private val contentResolver: ContentResolver = mock()

    private val underTest = TakePersistableUriPermissionUseCase(contentResolver)

    @Test
    fun takes() {
        val uri = Uri.parse("content://shit")

        underTest.invoke(uri)

        verify(contentResolver).takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    @Test
    fun consumesSecurityException() {
        val uri = Uri.parse("content://shit")
        whenever(contentResolver.takePersistableUriPermission(any(), any()))
            .thenThrow(SecurityException())

        underTest.invoke(uri)

        // No exception
    }
}