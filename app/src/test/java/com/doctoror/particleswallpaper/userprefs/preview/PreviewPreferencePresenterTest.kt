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
package com.doctoror.particleswallpaper.userprefs.preview

import android.app.Activity
import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.app.base.OnActivityResultCallback
import com.doctoror.particleswallpaper.presentation.config.ConfigFragment
import com.doctoror.particleswallpaper.presentation.view.MvpView
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.jupiter.api.Test

class PreviewPreferencePresenterTest {

    private val activity: Activity = mock()
    private val view: MvpView = mock()

    private val useCase: OpenChangeWallpaperIntentUseCase = mock()

    private val underTest = PreviewPreferencePresenter(activity).apply {
        onTakeView(view)
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
    fun finishesOnWallpaperChange() {
        // Given
        val callback = setHostAndExtractOnActivityResultCallback()

        // When
        callback.onActivityResult(REQUEST_CODE_CHANGE_WALLPAPER, Activity.RESULT_OK, null)

        // Then
        verify(activity).finish()
    }

    @Test
    fun doesNotFinishWhenWallpaperNotChanged() {
        // Given
        val callback = setHostAndExtractOnActivityResultCallback()

        // When
        callback.onActivityResult(REQUEST_CODE_CHANGE_WALLPAPER, Activity.RESULT_CANCELED, null)

        // Then
        verify(activity, never()).finish()
    }

    @Test
    fun opensChangeWallpaper() {
        // Given
        val useCaseSource = spy(Single.just(Unit))
        whenever(useCase.useCase()).thenReturn(useCaseSource)
        underTest.useCase = useCase

        // When
        underTest.onClick()

        // Then
        verify(useCaseSource).subscribe()
    }
}
