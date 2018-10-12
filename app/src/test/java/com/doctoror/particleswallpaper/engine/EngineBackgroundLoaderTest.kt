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
package com.doctoror.particleswallpaper.engine

import android.graphics.Bitmap
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.FutureTarget
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.util.Optional
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EngineBackgroundLoaderTest {

    private val requestManager: RequestManager = mock()

    private val settings: SceneSettings = mock {
        on { it.observeBackgroundUri() }.thenReturn(Observable.just(NO_URI))
    }

    private val schedulers = TrampolineSchedulers()

    private val underTest = EngineBackgroundLoader(
        requestManager, settings, schedulers
    )

    @After
    fun tearDown() {
        underTest.onDestroy()
        StandAloneContext.stopKoin()
    }

    @Test
    fun returnsNullBackgroundWhenNoUri() {
        underTest.onCreate()

        val o = underTest.observeBackground().test()
        underTest.setDimensions(1, 1)

        o.assertValue(Optional<Bitmap>(null))
    }

    @Test
    fun loadsBackgroundImage() {
        // Given
        val uri = "uri://scheme"

        val width = 1
        val height = 2

        val bitmap: Bitmap = mock()

        whenever(settings.observeBackgroundUri())
            .thenReturn(Observable.just(uri))

        givenMockRequestBuilderThatLoadsBitmapForParameters(
            uri,
            bitmap,
            width,
            height
        )

        // When
        underTest.onCreate()

        val o = underTest.observeBackground().test()

        underTest.setDimensions(width, height)

        // Then
        o.assertValue(Optional(bitmap))
    }

    @Test
    fun reloadsBackgroundImageOnDimensionsChange() {
        // Given
        val uri = "uri"
        val bitmap: Bitmap = mock()
        whenever(settings.observeBackgroundUri())
            .thenReturn(Observable.just(uri))

        // When
        underTest.onCreate()

        val o = underTest.observeBackground().test()

        givenMockRequestBuilderThatLoadsBitmapForParameters(
            uri,
            bitmap,
            1,
            1
        )

        underTest.setDimensions(1, 1)

        givenMockRequestBuilderThatLoadsBitmapForParameters(
            uri,
            bitmap,
            2,
            2
        )

        underTest.setDimensions(2, 2)

        // Then
        o.assertValues(Optional(bitmap), Optional(bitmap))
    }

    @Test
    fun reloadsBackgroundImageOnUriChange() {
        // Given
        val uri1 = "uri1"
        val uri2 = "uri2"

        val bitmap1: Bitmap = mock()
        val bitmap2: Bitmap = mock()

        val width = 1
        val height = 2

        val uriSubject = BehaviorSubject.create<String>()
        uriSubject.onNext(uri1)

        whenever(settings.observeBackgroundUri())
            .thenReturn(uriSubject)

        givenMockRequestBuilderThatLoadsBitmapForParameters(
            uri1,
            bitmap1,
            width,
            height
        )

        // When
        underTest.onCreate()

        val o = underTest.observeBackground().test()

        underTest.setDimensions(width, height)

        givenMockRequestBuilderThatLoadsBitmapForParameters(
            uri2,
            bitmap2,
            width,
            height
        )

        uriSubject.onNext(uri2)

        // Then
        o.assertValues(Optional(bitmap1), Optional(bitmap2))
    }

    private fun givenMockRequestBuilderThatLoadsBitmapForParameters(
        uri: String,
        bitmap: Bitmap,
        width: Int,
        height: Int
    ) {
        val requestBuilder: RequestBuilder<Bitmap> = mock()
        whenever(requestBuilder.apply(any())).thenReturn(requestBuilder)
        whenever(requestBuilder.load(uri)).thenReturn(requestBuilder)

        val futureTarget: FutureTarget<Bitmap> = mock {
            on { it.get() }.thenReturn(bitmap)
        }

        whenever(requestBuilder.submit(width, height)).thenReturn(futureTarget)

        whenever(requestManager.asBitmap()).thenReturn(requestBuilder)
    }
}
