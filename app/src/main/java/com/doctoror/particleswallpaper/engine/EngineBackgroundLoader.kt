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
import com.doctoror.particleswallpaper.framework.util.Optional
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class EngineBackgroundLoader(
    private val requestManager: RequestManager,
    private val settings: SceneSettings,
    private val schedulers: SchedulersProvider
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
    private fun imageLoadSettingsSource(size: Size) = settings
        .observeBackgroundUri()
        .map {
            ImageLoadRequest(
                size.width,
                size.height,
                it
            )
        }

    @WorkerThread
    private fun loadResource(request: ImageLoadRequest): Bitmap {
        return requestManager
            .asBitmap()
            .load(request.uri)
            .apply(RequestOptions.noAnimation())
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .apply(RequestOptions.centerCropTransform())
            .submit(request.width, request.height)
            .get()
    }

    private data class ImageLoadRequest(
        val width: Int,
        val height: Int,
        val uri: String
    )

    private data class Size(val width: Int, val height: Int)
}
