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
package com.doctoror.particleswallpaper.userprefs.bgimage

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.util.AttributeSet
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.app.Preference
import com.doctoror.particleswallpaper.framework.app.FragmentHolder
import com.doctoror.particleswallpaper.framework.di.inject
import org.koin.core.parameter.parametersOf

/**
 * Preferece for picking or clearing the background image
 */
class BackgroundImagePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Preference(context, attrs), BackgroundImagePreferenceView, FragmentHolder {

    private val presenter: BackgroundImagePreferencePresenter by inject(
        context = context,
        parameters = { parametersOf(this as BackgroundImagePreferenceView) }
    )

    override var fragment: Fragment? = null
        set(f) {
            presenter.host = f
            field = f
        }

    override fun onClick() {
        super.onClick()
        presenter.onClick()
    }

    override fun showActionDialog() {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton(R.string.Pick) { _, _ -> presenter.pickBackground() }
            .setNeutralButton(R.string.Clear) { _, _ -> presenter.clearBackground() }
            .setNegativeButton(R.string.Cancel, null)
            .show()
    }
}
