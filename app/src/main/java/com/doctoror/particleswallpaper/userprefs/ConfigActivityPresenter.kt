/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.userprefs

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.WorkerThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctoror.particlesdrawable.contract.SceneConfiguration
import com.doctoror.particlesdrawable.contract.SceneController
import com.doctoror.particleswallpaper.app.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.engine.configurator.SceneConfigurator
import com.doctoror.particleswallpaper.framework.execution.SchedulersProvider
import com.doctoror.particleswallpaper.framework.util.Optional
import com.doctoror.particleswallpaper.framework.view.removeOnGlobalLayoutListenerCompat
import com.doctoror.particleswallpaper.framework.view.setBackgroundCompat
import com.doctoror.particleswallpaper.userprefs.data.NO_URI
import com.doctoror.particleswallpaper.userprefs.data.SceneSettings
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

open class ConfigActivityPresenter(
    private val activity: Activity,
    private val schedulers: SchedulersProvider,
    private val configurator: SceneConfigurator,
    private val requestManager: RequestManager,
    private val settings: SceneSettings,
    private val view: ConfigActivityView,
    private val themeAttrColorResolver: ThemeAttrColorResolver = ThemeAttrColorResolver()
) : LifecycleObserver {

    private var bgDisposable: Disposable? = null

    var configuration: SceneConfiguration? = null

    var controller: SceneController? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        val configuration = configuration ?: throw IllegalStateException("configuration not set")
        val controller = controller ?: throw IllegalStateException("controller not set")

        val bg = view.getBackgroundView()
        if (bg.width == 0 || bg.height == 0) {
            bg.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {
                    bg.viewTreeObserver.removeOnGlobalLayoutListenerCompat(this)
                    subscribeToBackgroundLoad()
                }
            })
        } else {
            subscribeToBackgroundLoad()
        }

        configurator.subscribe(configuration, controller, settings, schedulers.mainThread())
    }

    private fun subscribeToBackgroundLoad() {
        bgDisposable = Observable
            .combineLatest(
                settings.observeBackgroundColor(),
                newBackgroundImageLoader(),
                BiFunction<Int, Optional<Drawable>, BackgroundData> { color, image ->
                    BackgroundData(color, image)
                }
            )
            .observeOn(schedulers.mainThread())
            .subscribe { applyBackground(it) }
    }

    private fun newBackgroundImageLoader() = settings
        .observeBackgroundUri()
        .observeOn(schedulers.mainThread())
        .flatMap { uri ->
            if (uri == NO_URI) {
                Observable.just(Optional<Drawable>(null))
            } else {
                val bg = view.getBackgroundView()
                Observable
                    .fromCallable { loadBackgroundImage(uri, bg.width, bg.height) }
                    .subscribeOn(schedulers.io())
            }
        }

    @WorkerThread
    private fun loadBackgroundImage(uri: String, width: Int, height: Int): Optional<Drawable> {
        val target = requestManager.load(uri)
            .apply(RequestOptions.noAnimation())
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .apply(RequestOptions.centerCropTransform())
            .submit(width, height)

        return Optional(
            try {
                target.get()
            } catch (e: RuntimeException) {
                null
            }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        bgDisposable?.dispose()
        configurator.dispose()
    }

    open fun onCreateOptionsMenu(menu: Menu) = false

    open fun onOptionsItemSelected(item: MenuItem) = false

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CODE_CHANGE_WALLPAPER && resultCode == Activity.RESULT_OK) {
            activity.finish()
        }
    }

    private fun applyBackground(bgData: BackgroundData) {
        applyBackground(view.getBackgroundView(), bgData.image.value, bgData.color)
    }

    private fun applyBackgroundColor(bg: ImageView, @ColorInt color: Int) =
        bg.setBackgroundCompat(
            if (colorIsWindowBackground(bg.context, color)) null
            else ColorDrawable(color)
        )

    private fun colorIsWindowBackground(context: Context, @ColorInt color: Int) = try {
        color == themeAttrColorResolver.getColor(context.theme, android.R.attr.windowBackground)
    } catch (e: UnsupportedOperationException) {
        // Can happen on some themes
        Log.w("ConfigPresenter", e)
        false
    }

    private fun applyBackground(target: ImageView, drawable: Drawable?, @ColorInt color: Int) {
        if (drawable == null) {
            applyNoImageBackground(target, color)
        } else {
            applyImageBackground(target, drawable, color)
        }
    }

    private fun applyNoImageBackground(target: ImageView, @ColorInt color: Int) {
        target.setImageDrawable(null)
        applyBackgroundColor(target, color)
    }

    private fun applyImageBackground(target: ImageView, drawable: Drawable, @ColorInt color: Int) {
        if (drawable is BitmapDrawable && drawable.bitmap?.hasAlpha() == true) {
            applyBackgroundColor(target, color)
        } else {
            target.setBackgroundCompat(null)
        }
        target.setImageDrawable(drawable)
    }

    private data class BackgroundData(
        @ColorInt
        val color: Int,
        val image: Optional<Drawable>
    )
}
