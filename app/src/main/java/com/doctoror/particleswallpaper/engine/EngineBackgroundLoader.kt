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
import androidx.annotation.WorkerThread
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.glide.CenterCropAndThenResizeTransform
import com.doctoror.particleswallpaper.framework.util.Optional
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class EngineBackgroundLoader(
    private val requestManager: RequestManager,
    private val settings: SceneSettings,
    private val settingsOpenGL: OpenGlSettings,
    private val schedulers: SchedulersProvider,
    private val textureDimensionsCalculator: TextureDimensionsCalculator
) {

    private val dimensionsSubject = PublishSubject.create<Size>()
    private val imageLoadRequestSubject = PublishSubject.create<ImageLoadRequest>()

    private var disposables = CompositeDisposable()

    fun onCreate() {
        disposables.add(dimensionsSubject
            .observeOn(schedulers.io())
            .flatMap { imageLoadSettingsSource(it) }
            .subscribe { imageLoadRequestSubject.onNext(it) })
    }

    fun onDestroy() {
        disposables.clear()
    }

    fun setDimensions(width: Int, height: Int) {
        dimensionsSubject.onNext(Size(width, height))
    }

    fun observeBackground(): Observable<Optional<Bitmap>> = imageLoadRequestSubject
        .distinctUntilChanged()
        .observeOn(schedulers.io())
        .map { request ->
            Optional(
                if (request.uri == NO_URI) {
                    null
                } else {
                    loadResource(request)
                }
            )
        }

    @WorkerThread
    private fun imageLoadSettingsSource(size: Size) = Observable
        .combineLatest(
            settingsOpenGL.observeOptimizeTextures(),
            settings.observeBackgroundUri(),
            BiFunction<Boolean, String, ImageLoadRequest> { optimize, uri ->
                ImageLoadRequest(
                    size.width,
                    size.height,
                    optimize,
                    uri
                )
            }
        )

    @WorkerThread
    private fun loadResource(request: ImageLoadRequest): Bitmap {
        val targetDimensions = textureDimensionsCalculator
            .calculateTextureDimensions(request.width, request.height, request.optimize)

        return requestManager
            .asBitmap()
            .load(request.uri)
            .apply(RequestOptions.noAnimation())
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .apply(makeTransformOptions(request, targetDimensions))
            .submit(targetDimensions.first, targetDimensions.second)
            .get()
    }

    @WorkerThread
    private fun makeTransformOptions(
        request: ImageLoadRequest,
        targetDimensions: android.util.Pair<Int, Int>
    ) =
        if (targetDimensions.first != request.width || targetDimensions.second != request.height) {
            RequestOptions.bitmapTransform(
                CenterCropAndThenResizeTransform(
                    targetDimensions.first,
                    targetDimensions.second
                )
            )
        } else {
            RequestOptions.centerCropTransform()
        }

    private data class ImageLoadRequest(
        val width: Int,
        val height: Int,
        val optimize: Boolean,
        val uri: String
    )

    private data class Size(val width: Int, val height: Int)
}
