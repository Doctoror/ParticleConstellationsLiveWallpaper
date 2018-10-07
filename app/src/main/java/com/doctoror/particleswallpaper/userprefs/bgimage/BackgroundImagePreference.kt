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

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.os.Build
import android.preference.Preference
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.app.FragmentHolder
import com.doctoror.particleswallpaper.framework.di.components.AppComponentProvider
import com.doctoror.particleswallpaper.framework.di.components.DaggerPreferenceComponent
import javax.inject.Inject

/**
 * Preferece for picking or clearing the background image
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
@RequiresApi(Build.VERSION_CODES.KITKAT)
class BackgroundImagePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Preference(context, attrs), BackgroundImagePreferenceView, FragmentHolder {

    @Inject
    lateinit var presenter: BackgroundImagePreferencePresenter

    override var fragment: Fragment? = null
        set(f) {
            presenter.host = f
            field = f
        }

    init {
        DaggerPreferenceComponent.builder()
            .appComponent(AppComponentProvider.provideAppComponent(context))
            .build()
            .inject(this)

        isPersistent = false
        presenter.onTakeView(this)
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
