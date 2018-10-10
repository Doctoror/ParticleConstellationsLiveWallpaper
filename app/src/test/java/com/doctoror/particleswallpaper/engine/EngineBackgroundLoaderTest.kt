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
import android.util.Pair
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.FutureTarget
import com.doctoror.particleswallpaper.framework.execution.TrampolineSchedulers
import com.doctoror.particleswallpaper.framework.util.Optional
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.inOrder
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

    private val defaultTargetWidth = 64
    private val defaultTargetHeight = 128

    private val requestManager: RequestManager = mock()

    private val settings: SceneSettings = mock {
        on { it.observeBackgroundUri() }.thenReturn(Observable.just(NO_URI))
    }

    private val settingsOpenGL: OpenGlSettings = mock {
        on { it.observeOptimizeTextures() }.thenReturn(Observable.just(false))
    }

    private val schedulers = TrampolineSchedulers()

    private val textureDimensionsCalculator: TextureDimensionsCalculator = mock {
        on { it.calculateTextureDimensions(any(), any(), any()) }
            .thenReturn(Pair(defaultTargetWidth, defaultTargetHeight))
    }

    private val underTest = EngineBackgroundLoader(
        requestManager, settings, settingsOpenGL, schedulers, textureDimensionsCalculator
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
        val optimizeTexutres = true

        val width = 1
        val height = 2

        val targetWidth = 3
        val targetHeight = 4

        val bitmap: Bitmap = mock()

        whenever(settings.observeBackgroundUri())
            .thenReturn(Observable.just(uri))

        whenever(settingsOpenGL.observeOptimizeTextures())
            .thenReturn(Observable.just(optimizeTexutres))

        whenever(
            textureDimensionsCalculator.calculateTextureDimensions(
                width,
                height,
                optimizeTexutres
            )
        ).thenReturn(Pair(targetWidth, targetHeight))

        givenMockRequestBuilderThatLoadsBitmapForParameters(
            uri,
            bitmap,
            targetWidth,
            targetHeight
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

        givenMockRequestBuilderThatLoadsBitmapForParameters(
            uri,
            bitmap,
            defaultTargetWidth,
            defaultTargetHeight
        )

        // When
        underTest.onCreate()

        val o = underTest.observeBackground().test()

        underTest.setDimensions(1, 1)
        underTest.setDimensions(2, 2)

        // Then
        val inorder = inOrder(textureDimensionsCalculator)
        inorder.verify(textureDimensionsCalculator).calculateTextureDimensions(1, 1, false)
        inorder.verify(textureDimensionsCalculator).calculateTextureDimensions(2, 2, false)

        o.assertValues(Optional(bitmap), Optional(bitmap))
    }

    @Test
    fun reloadsBackgroundImageOnTextureOptimizationChange() {
        // Given
        val uri = "uri"
        val bitmap: Bitmap = mock()

        val width = 1
        val height = 2

        val textureOptimizationSubject = BehaviorSubject.create<Boolean>()
        textureOptimizationSubject.onNext(true)

        whenever(settings.observeBackgroundUri())
            .thenReturn(Observable.just(uri))

        whenever(settingsOpenGL.observeOptimizeTextures())
            .thenReturn(textureOptimizationSubject)

        givenMockRequestBuilderThatLoadsBitmapForParameters(
            uri,
            bitmap,
            defaultTargetWidth,
            defaultTargetHeight
        )

        // When
        underTest.onCreate()

        val o = underTest.observeBackground().test()

        underTest.setDimensions(width, height)
        textureOptimizationSubject.onNext(false)

        // Then
        val inorder = inOrder(textureDimensionsCalculator)
        inorder.verify(textureDimensionsCalculator).calculateTextureDimensions(width, height, true)
        inorder.verify(textureDimensionsCalculator).calculateTextureDimensions(width, height, false)

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
            defaultTargetWidth,
            defaultTargetHeight
        )

        // When
        underTest.onCreate()

        val o = underTest.observeBackground().test()

        underTest.setDimensions(width, height)

        givenMockRequestBuilderThatLoadsBitmapForParameters(
            uri2,
            bitmap2,
            defaultTargetWidth,
            defaultTargetHeight
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
