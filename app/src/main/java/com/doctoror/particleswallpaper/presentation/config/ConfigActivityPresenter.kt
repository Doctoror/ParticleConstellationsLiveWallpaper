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
package com.doctoror.particleswallpaper.presentation.config

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_CHANGE_WALLPAPER
import com.doctoror.particleswallpaper.presentation.extensions.removeOnGlobalLayoutListenerCompat
import com.doctoror.particleswallpaper.presentation.extensions.setBackgroundCompat
import com.doctoror.particleswallpaper.presentation.presenter.Presenter
import com.doctoror.particleswallpaper.presentation.util.ThemeUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

/**
 * Created by Yaroslav Mytkalyk on 17.06.17.
 *
 * [ConfigActivity] presenter.
 */
open class ConfigActivityPresenter(
        private val schedulers: SchedulersProvider,
        private val configurator: SceneConfigurator,
        private val requestManager: RequestManager,
        private val settings: SettingsRepository)
    : Presenter<ConfigActivityView>, LifecycleObserver {

    private val particlesDrawable = ParticlesDrawable()

    protected lateinit var view: ConfigActivityView

    private var bgDisposable: Disposable? = null

    override fun onTakeView(view: ConfigActivityView) {
        this.view = view
        view.setContainerBackground(particlesDrawable)
    }

    open fun onCreateOptionsMenu(menu: Menu) = false

    open fun onOptionsItemSelected(item: MenuItem) = false

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun onStart() {
        bgDisposable = Observable.combineLatest(
                settings.getBackgroundUri(),
                settings.getBackgroundColor(),
                BiFunction<String, Int, Pair<String, Int>> { t1, t2 -> Pair(t1, t2) })
                .observeOn(schedulers.mainThread())
                .subscribe { applyBackground(it) }
        configurator.subscribe(particlesDrawable, settings)
        particlesDrawable.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun onStop() {
        bgDisposable?.dispose()
        particlesDrawable.stop()
        configurator.dispose()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CODE_CHANGE_WALLPAPER && resultCode == Activity.RESULT_OK) {
            view.getActivity().finish()
        }
    }

    private fun applyBackground(result: Pair<String, Int>) {
        applyBackground(result.first, result.second)
    }

    private fun applyBackground(uri: String, @ColorInt color: Int) {
        val bg = view.getActivity().findViewById<ImageView>(R.id.bg)
        if (uri == NO_URI) {
            requestManager.clear(bg)
            applyNoImageBackground(bg, color)
        } else if (bg.width != 0 && bg.height != 0) {
            val target = ImageLoadTarget(bg, color)
            requestManager.load(uri)
                    .apply(RequestOptions.noAnimation())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.centerCropTransform())
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean): Boolean {
                            applyNoImageBackground(bg, color)
                            return true
                        }

                        override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean) = false
                    })
                    .into(target)
        } else {
            bg.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {
                    bg.viewTreeObserver.removeOnGlobalLayoutListenerCompat(this)
                    applyBackground(uri, color)
                }
            })
        }
    }

    private fun applyBackgroundColor(bg: ImageView, @ColorInt color: Int) =
            bg.setBackgroundCompat(
                    if (colorIsWindowBackground(bg.context, color)) null
                    else ColorDrawable(color))

    private fun colorIsWindowBackground(context: Context, @ColorInt color: Int) = try {
        color == ThemeUtils.getColor(context.theme, android.R.attr.windowBackground)
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

    private inner class ImageLoadTarget(private val target: ImageView,
                                        @ColorInt private val bgColor: Int)
        : SimpleTarget<Drawable>(target.width, target.height) {

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            applyBackground(target, resource, bgColor)
        }
    }
}
