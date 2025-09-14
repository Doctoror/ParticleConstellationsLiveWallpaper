package com.doctoror.particleswallpaper.userprefs.bgimage

import android.app.Application
import android.content.ContentResolver
import android.content.Intent
import android.content.UriPermission
import android.net.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class ReleasePersistableUriPermissionUseCaseTest {

    private val contentResolver: ContentResolver = mock()

    private val underTest = ReleasePersistableUriPermissionUseCase(contentResolver)

    @Test
    fun doesNotReleaseUriPermissionsWhenContentResolverHasNoPermissions() {
        // Given
        val contentResolver: ContentResolver = mock()
        val uri = Uri.parse("content://shithost")

        // When
        underTest.invoke(uri)

        // Then
        verify(contentResolver, never()).releasePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    @Test
    fun releasesUriPermissions() {
        // Given
        val uri = Uri.parse("content://shithost")
        val uriPermission: UriPermission = mock { on(it.uri).doReturn(uri) }
        whenever(contentResolver.persistedUriPermissions).doReturn(listOf(uriPermission))

        // When
        underTest.invoke(uri)

        // Then
        verify(contentResolver).releasePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    @Test
    fun consumesSecurityException() {
        // Given
        val uri = Uri.parse("content://shithost")
        whenever(contentResolver.releasePersistableUriPermission(any(), any()))
            .thenThrow(SecurityException())

        // When
        underTest.invoke(uri)

        // Then no exception
    }
}