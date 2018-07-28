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
package com.doctoror.particleswallpaper.presentation.presenter

import android.app.Activity
import android.content.*
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.data.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.domain.config.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.file.BackgroundImageManager
import com.doctoror.particleswallpaper.domain.interactor.PickImageDocumentUseCase
import com.doctoror.particleswallpaper.domain.interactor.PickImageGetContentUseCase
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_GET_CONTENT
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_OPEN_DOCUMENT
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallback
import com.doctoror.particleswallpaper.presentation.config.ConfigFragment
import com.doctoror.particleswallpaper.presentation.view.BackgroundImagePreferenceView
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class BackgroundImagePreferencePresenterTest {

    private val apiLevelProvider: ApiLevelProvider = mock {
        on(it.provideSdkInt()).doReturn(Build.VERSION_CODES.O_MR1)
    }

    private val context: Context = mock()
    private val glide: Glide = mock()
    private val pickImageGetContentUseCase: PickImageGetContentUseCase = mock()
    private val pickImageDocumentUseCase: PickImageDocumentUseCase = mock()
    private val settings: MutableSettingsRepository = mock()
    private val defaults: SettingsRepository = mock()
    private val backgroundImageManager: BackgroundImageManager = mock()
    private val view: BackgroundImagePreferenceView = mock()

    private val underTest = newBackgrodundImagePreferencePresenter()

    private fun newBackgrodundImagePreferencePresenter() = BackgroundImagePreferencePresenter(
            apiLevelProvider,
            context,
            glide,
            pickImageGetContentUseCase,
            pickImageDocumentUseCase,
            TrampolineSchedulers(),
            settings,
            defaults,
            backgroundImageManager).apply {
        onTakeView(view)
    }

    @Before
    fun setup() {
        whenever(defaults.getBackgroundUri()).thenReturn(Observable.just(NO_URI))
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(NO_URI))
    }

    private fun setHostAndExtractOnActivityResultCallback(
            host: ConfigFragment = mock()): OnActivityResultCallback {
        underTest.host = host
        val callbackCapturer = argumentCaptor<OnActivityResultCallback>()
        verify(host).registerCallback(callbackCapturer.capture())

        return callbackCapturer.firstValue
    }

    @Test
    fun registersOnActivityResultCallback() {
        // Given
        val host: ConfigFragment = mock()

        // When
        underTest.host = host

        // Then
        verify(host).registerCallback(any())
    }

    @Test
    fun unregistersOnActivityResultCallbackOnHostChange() {
        // Given
        val host: ConfigFragment = mock()
        val callback = setHostAndExtractOnActivityResultCallback(host)

        // When
        underTest.host = null

        // Then
        verify(host).unregsiterCallback(callback)
    }

    @Test
    fun showsActionDialogOnClick() {
        // When
        underTest.onClick()

        // Then
        verify(view).showActionDialog()
    }

    @Test
    fun setsDefaultBackgroundUriOnClearBackground() {
        // When
        underTest.clearBackground()

        // Then
        verify(settings).setBackgroundUri(NO_URI)
    }

    @Test
    fun clearsBackgroundImage() {
        // When
        underTest.clearBackground()

        // Then
        verify(backgroundImageManager).clearBackgroundImage()
    }

    @Test
    fun clearsGlideMemoryOnClearBackgroundImage() {
        // When
        underTest.clearBackground()

        // Then
        verify(glide).clearMemory()
    }

    @Test
    fun releasesUriPermissionsOnClearBackground() {
        // Given
        val contentResolver: ContentResolver = mock()
        val uri = Uri.parse("content://shithost")

        givenBackgroundUriThatNeedsReleasingPermissions(contentResolver, uri)

        // When
        underTest.clearBackground()

        // Then
        verify(contentResolver).releasePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    @Test
    fun doesNotReleaseUriPermissionsWhenPreKitKat() {
        // Given
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.JELLY_BEAN_MR2)
        val underTest = newBackgrodundImagePreferencePresenter()

        val contentResolver: ContentResolver = mock()
        val uri = Uri.parse("content://shithost")

        givenBackgroundUriThatNeedsReleasingPermissions(contentResolver, uri)

        // When
        underTest.clearBackground()

        // Then
        verify(contentResolver, never()).releasePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    @Test
    fun doesNotReleaseUriPermissionsWhenContentResolverHasNoPermissions() {
        // Given
        val contentResolver: ContentResolver = mock()
        val uri = Uri.parse("content://shithost")

        whenever(context.contentResolver).thenReturn(contentResolver)
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(uri.toString()))

        // When
        underTest.clearBackground()

        // Then
        verify(contentResolver, never()).releasePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    @Test
    fun doesNotCrashOnReleaseUriPermissionsWhenContentResolverNotSet() {
        // Given
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just("content://shithost"))

        // When
        underTest.clearBackground()

        // Then no crash
    }

    fun doesNotCheckForUriPermissionsWhenUriNotSet() {
        // Given
        val contentResolver: ContentResolver = mock()
        whenever(context.contentResolver).thenReturn(contentResolver)
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(NO_URI))

        // When
        underTest.clearBackground()

        // Then
        verify(contentResolver, never()).persistedUriPermissions
    }

    private fun givenBackgroundUriThatNeedsReleasingPermissions(
            contentResolver: ContentResolver, uri: Uri) {
        val uriPermission: UriPermission = mock {
            on(it.uri).doReturn(uri)
        }

        whenever(contentResolver.persistedUriPermissions).doReturn(listOf(uriPermission))
        whenever(context.contentResolver).thenReturn(contentResolver)
        whenever(settings.getBackgroundUri()).thenReturn(Observable.just(uri.toString()))
    }

    @Test
    fun picksBackgroundByOpenDocument() {
        // When
        underTest.host = mock()
        underTest.pickBackground()

        // Then
        verify(pickImageDocumentUseCase).invoke(any())
    }

    @Test
    fun handlesActivityNotFoundExceptionForOpenDocument() {
        // Given
        givenContextHasResourcesWithStrings()
        whenever(pickImageDocumentUseCase.invoke(any())).thenThrow(ActivityNotFoundException())

        // When
        underTest.host = mock()
        underTest.pickBackground()

        // Then RuntimeException is not propagated
    }

    @Test
    fun picksBackgroundByGetContentForLegacyApi() {
        // Given
        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.JELLY_BEAN_MR2)
        val underTest = newBackgrodundImagePreferencePresenter()

        // When
        underTest.host = mock()
        underTest.pickBackground()

        // Then
        verify(pickImageGetContentUseCase).invoke(any())
    }

    @Test
    fun handlesActivityNotFoundExceptionForGetContent() {
        // Given
        givenContextHasResourcesWithStrings()
        whenever(pickImageDocumentUseCase.invoke(any())).thenThrow(ActivityNotFoundException())

        whenever(apiLevelProvider.provideSdkInt()).thenReturn(Build.VERSION_CODES.JELLY_BEAN_MR2)
        val underTest = newBackgrodundImagePreferencePresenter()

        // When
        underTest.host = mock()
        underTest.pickBackground()

        // Then RuntimeException is not propagated
    }

    @Test
    fun clearsGlideMemoryOnImageResult() {
        // Given
        val callback = setHostAndExtractOnActivityResultCallback()
        val uri = Uri.parse("content://shit")

        // When
        callback.onActivityResult(REQUEST_CODE_OPEN_DOCUMENT, Activity.RESULT_OK, Intent().apply {
            data = uri
        })

        // Then
        verify(glide).clearMemory()
    }

    @Test
    fun takesPersistableUriPermissionAndSetsBackgroundOnOpenDocumentResult() {
        // Given
        val callback = setHostAndExtractOnActivityResultCallback()
        val uri = Uri.parse("content://shit")

        val contentResolver: ContentResolver = mock()
        whenever(context.contentResolver).thenReturn(contentResolver)

        // When
        callback.onActivityResult(REQUEST_CODE_OPEN_DOCUMENT, Activity.RESULT_OK, Intent().apply {
            data = uri
        })

        // Then
        verify(contentResolver).takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        verify(settings).setBackgroundUri(uri.toString())
    }

    @Test
    fun copiesBackgroundToFileAndSetsBackgroundOnGetContentResult() {
        // Given
        val callback = setHostAndExtractOnActivityResultCallback()
        val uri = Uri.parse("content://shit")

        val localUri = Uri.parse("file://localshit")
        whenever(backgroundImageManager.copyBackgroundToFile(uri)).thenReturn(localUri)

        // When
        callback.onActivityResult(REQUEST_CODE_GET_CONTENT, Activity.RESULT_OK, Intent().apply {
            data = uri
        })

        // Then
        verify(settings).setBackgroundUri(localUri.toString())
    }

    @Test
    fun storesDirectUriWhenCopyFailedOnGetContent() {
        // Given
        val callback = setHostAndExtractOnActivityResultCallback()
        val uri = Uri.parse("content://shit")

        whenever(backgroundImageManager.copyBackgroundToFile(uri))
                .thenThrow(RuntimeException())

        // When
        callback.onActivityResult(REQUEST_CODE_GET_CONTENT, Activity.RESULT_OK, Intent().apply {
            data = uri
        })

        // Then
        verify(settings).setBackgroundUri(uri.toString())
    }

    private fun givenContextHasResourcesWithStrings() {
        val resources: Resources = mock {
            on(it.getString(any())).doReturn("")
        }

        whenever(context.resources).thenReturn(resources)
    }
}
