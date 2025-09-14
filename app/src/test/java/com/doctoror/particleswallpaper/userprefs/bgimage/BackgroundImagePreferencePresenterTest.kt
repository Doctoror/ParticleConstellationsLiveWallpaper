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
package com.doctoror.particleswallpaper.userprefs.bgimage

import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import com.bumptech.glide.Glide
import com.doctoror.particleswallpaper.app.REQUEST_CODE_PICK_IMAGE
import com.doctoror.particleswallpaper.framework.lifecycle.OnActivityResultCallback
import com.doctoror.particleswallpaper.userprefs.ConfigFragment
import com.doctoror.particleswallpaper.userprefs.data.DefaultSceneSettings
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class BackgroundImagePreferencePresenterTest {

    private val context: Context = mock()
    private val glide: Glide = mock()
    private val pickImageUseCase: PickImageUseCase = mock()
    private val releasePersistableUriPermissionUseCase: ReleasePersistableUriPermissionUseCase =
        mock()
    private val settings: SceneSettings = mock()
    private val takePersistableUriPermissionUseCase: TakePersistableUriPermissionUseCase =
        mock()
    private val defaults: DefaultSceneSettings = mock()
    private val view: BackgroundImagePreferenceView = mock()

    private val underTest = newBackgrodundImagePreferencePresenter()

    private fun newBackgrodundImagePreferencePresenter() = BackgroundImagePreferencePresenter(
        context,
        defaults,
        glide,
        pickImageUseCase,
        releasePersistableUriPermissionUseCase,
        settings,
        takePersistableUriPermissionUseCase,
        view
    )

    @Before
    fun setup() {
        whenever(defaults.backgroundUri).thenReturn(NO_URI)
        whenever(settings.backgroundUri).thenReturn(NO_URI)
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(NO_URI))
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    private fun setHostAndExtractOnActivityResultCallback(
        host: ConfigFragment = mock()
    ): OnActivityResultCallback {
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
        whenever(settings.backgroundUri).thenReturn("content://ass")

        // When
        underTest.clearBackground()

        // Then
        verify(settings).backgroundUri = NO_URI
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
        val uri = Uri.parse("content://shithost")
        whenever(settings.backgroundUri).thenReturn(uri.toString())

        // When
        underTest.clearBackground()

        // Then
        verify(releasePersistableUriPermissionUseCase).invoke(uri)
    }

    @Test
    fun releasesUriPermissionsOnBackgroundChange() {
        // Given
        val prevUri = Uri.parse("content://shithost")
        whenever(settings.backgroundUri).thenReturn(prevUri.toString())

        val callback = setHostAndExtractOnActivityResultCallback()
        val newUri = Uri.parse("content://shit")

        // When
        callback.onActivityResult(REQUEST_CODE_PICK_IMAGE, Activity.RESULT_OK, Intent().apply {
            data = newUri
        })

        // Then
        verify(releasePersistableUriPermissionUseCase).invoke(prevUri)
    }

    @Test
    fun doesNotReleaseUriPermissionsWhenUriNotSet() {
        // Given
        whenever(settings.observeBackgroundUri()).thenReturn(Observable.just(NO_URI))

        // When
        underTest.clearBackground()

        // Then
        verify(releasePersistableUriPermissionUseCase, never()).invoke(any())
    }

    @Test
    fun picksBackgroundByOpenDocument() {
        // When
        underTest.host = mock()
        underTest.pickBackground()

        // Then
        verify(pickImageUseCase).invoke(eq(context), any())
    }

    @Test
    fun handlesActivityNotFoundExceptionForOpenDocument() {
        // Given
        givenContextHasResourcesWithStrings()
        whenever(pickImageUseCase.invoke(eq(context), any())).thenThrow(ActivityNotFoundException())

        // When
        underTest.host = mock()
        underTest.pickBackground()

        // Then RuntimeException is not propagated
    }

    @Test
    fun handlesActivityNotFoundExceptionForImagePick() {
        // Given
        givenContextHasResourcesWithStrings()
        whenever(pickImageUseCase.invoke(eq(context), any())).thenThrow(ActivityNotFoundException())
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
        callback.onActivityResult(REQUEST_CODE_PICK_IMAGE, Activity.RESULT_OK, Intent().apply {
            data = uri
        })

        // Then
        verify(glide).clearMemory()
    }

    @Test
    fun takesPersistableUriPermissionAndSetsBackgroundOnPickImageResult() {
        // Given
        val callback = setHostAndExtractOnActivityResultCallback()
        val uri = Uri.parse("content://shit")

        // When
        callback.onActivityResult(REQUEST_CODE_PICK_IMAGE, Activity.RESULT_OK, Intent().apply {
            data = uri
        })

        // Then
        verify(takePersistableUriPermissionUseCase).invoke(uri)
        verify(settings).backgroundUri = uri.toString()
    }

    private fun givenContextHasResourcesWithStrings() {
        val resources: Resources = mock {
            on(it.getString(any())).doReturn("")
        }

        whenever(context.resources).thenReturn(resources)
    }
}
