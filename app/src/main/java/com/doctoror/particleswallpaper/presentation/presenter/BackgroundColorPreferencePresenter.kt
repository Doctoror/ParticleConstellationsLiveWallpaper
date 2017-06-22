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
package com.doctoror.particleswallpaper.presentation.presenter

import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.di.modules.DEFAULT
import com.doctoror.particleswallpaper.presentation.di.scopes.PerPreference
import com.doctoror.particleswallpaper.presentation.view.BackgroundColorPreferenceView
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Yaroslav Mytkalyk on 03.06.17.
 *
 * Presenter for [BackgroundColorPreferenceView]
 */
@PerPreference
class BackgroundColorPreferencePresenter @Inject constructor(
        private val schedulers: SchedulersProvider,
        private val settings: MutableSettingsRepository,
        private @Named(DEFAULT) val defaults: SettingsRepository)
    : Presenter<BackgroundColorPreferenceView> {

    private lateinit var view: BackgroundColorPreferenceView

    private var disposable: Disposable? = null

    private val changeAction = Consumer<Int> { t ->
        if (t != null) {
            view.setColor(t)
        }
    }

    override fun onTakeView(view: BackgroundColorPreferenceView) {
        this.view = view
    }

    fun onPreferenceChange(v: Int?) {
        val color = v ?: defaults.getBackgroundColor().blockingFirst()
        settings.setBackgroundColor(color)
        settings.setBackgroundUri(NO_URI)
    }

    fun onClick() {
        settings.getBackgroundUri()
                .take(1)
                .observeOn(schedulers.mainThread())
                .subscribe({ uri ->
                    if (uri == NO_URI) {
                        view.showPreferenceDialog()
                    } else {
                        view.showWarningDialog()
                    }
                })
    }

    override fun onStart() {
        disposable = settings.getBackgroundColor()
                .observeOn(schedulers.mainThread())
                .subscribe(changeAction)
    }

    override fun onStop() {
        disposable?.dispose()
    }
}