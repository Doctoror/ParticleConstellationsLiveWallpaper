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
package com.doctoror.particleswallpaper.userprefs.preview

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import com.doctoror.particleswallpaper.framework.app.FragmentHolder
import com.doctoror.particleswallpaper.framework.app.actions.FragmentStartActivityForResultAction
import com.doctoror.particleswallpaper.framework.di.inject
import org.koin.core.parameter.parametersOf

class PreviewPreference @JvmOverloads constructor(
    contextParam: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Preference(contextParam, attrs), FragmentHolder {

    private val intentProvider: OpenChangeWallpaperIntentProvider by inject(context = context)

    private val presenter: PreviewPreferencePresenter by inject(
        context = context,
        parameters = { parametersOf(contextParam as Activity) }
    )

    override var fragment: Fragment? = null
        set(f) {
            presenter.useCase = if (f != null) newOpenChangeWallpaperIntentUseCase(f) else null
            presenter.host = f
            field = f
        }

    init {
        isPersistent = false
    }

    @Deprecated("Must declare as deprecated when overriding deprecated api")
    override fun onClick() {
        presenter.onClick()
    }

    private fun newOpenChangeWallpaperIntentUseCase(host: Fragment) =
        OpenChangeWallpaperIntentUseCase(
            intentProvider,
            FragmentStartActivityForResultAction(host)
        )
}
