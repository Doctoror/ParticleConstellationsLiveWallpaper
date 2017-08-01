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

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.ads.AdsProvider
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.ads.AdViewFactory
import com.doctoror.particleswallpaper.presentation.extensions.setBackgroundCompat
import com.doctoror.particleswallpaper.presentation.presenter.Presenter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

/**
 * Created by Yaroslav Mytkalyk on 17.06.17.
 *
 * [ConfigActivity] presenter
 */
open class ConfigActivityPresenter(
        private val schedulers: SchedulersProvider,
        private val configurator: SceneConfigurator,
        private val adProvider: AdsProvider,
        private val settings: SettingsRepository,
        private val defaults: SettingsRepository)
    : Presenter<ConfigActivityView>, LifecycleObserver {

    private val particlesDrawable = ParticlesDrawable()

    private lateinit var glide: RequestManager

    protected lateinit var view: ConfigActivityView

    private var bgDisposable: Disposable? = null

    override fun onTakeView(view: ConfigActivityView) {
        this.view = view
        glide = Glide.with(view.getActivity())
        setBackground(view)
        initAdView(view)
    }

    private fun setBackground(view: ConfigActivityView) {
        view.getActivity().findViewById(R.id.drawableContainer)!!.setBackgroundCompat(particlesDrawable)
    }

    private fun initAdView(view: ConfigActivityView) {
        adProvider.initialize(AdViewFactory.makeAdView(
                view.getActivity().findViewById(android.R.id.content)))
    }

    open fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

    open fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun onStart() {
        adProvider.onStart()
        bgDisposable = Observable.combineLatest(
                settings.getBackgroundUri(),
                settings.getBackgroundColor(),
                BiFunction<String, Int, Pair<String, Int>> { t1, t2 -> Pair(t1, t2) })
                .observeOn(schedulers.mainThread())
                .subscribe({ result: Pair<String, Int> -> applyBackground(result) })
        configurator.subscribe(particlesDrawable, settings)
        particlesDrawable.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun onStop() {
        adProvider.onStop()
        bgDisposable?.dispose()
        particlesDrawable.stop()
        configurator.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        adProvider.onDestroy()
    }

    private fun applyBackground(result: Pair<String, Int>) {
        applyBackground(result.first, result.second)
    }

    private fun applyBackground(uri: String, @ColorInt color: Int) {
        val bg: ImageView = view.getActivity().findViewById(R.id.bg) as ImageView
        if (uri == NO_URI) {
            onNoBackgroundImage(bg, color)
        } else {
            glide.load(uri)
                    .apply(RequestOptions.noAnimation())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.centerCropTransform())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?,
                                                  model: Any?,
                                                  target: Target<Drawable>?,
                                                  isFirstResource: Boolean): Boolean {
                            onNoBackgroundImage(bg, color)
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?,
                                                     model: Any?,
                                                     target: Target<Drawable>?,
                                                     dataSource: DataSource?,
                                                     isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(bg)
        }
    }

    private fun onNoBackgroundImage(bg: ImageView, @ColorInt color: Int) {
        glide.clear(bg)
        bg.setImageDrawable(null)

        defaults.getBackgroundColor()
                .observeOn(schedulers.mainThread())
                .subscribe({ default ->
                    bg.setBackgroundCompat((if (color == default) null else ColorDrawable(color)))
                })
    }
}