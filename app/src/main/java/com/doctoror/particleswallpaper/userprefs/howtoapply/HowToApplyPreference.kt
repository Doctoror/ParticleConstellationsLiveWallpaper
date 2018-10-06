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
package com.doctoror.particleswallpaper.userprefs.howtoapply

import android.app.Fragment
import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import com.doctoror.particleswallpaper.presentation.di.components.AppComponentProvider
import com.doctoror.particleswallpaper.presentation.di.components.DaggerPreferenceComponent
import com.doctoror.particleswallpaper.app.FragmentHolder
import javax.inject.Inject

class HowToApplyPreference @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : Preference(context, attrs), HowToApplyPreferenceView, FragmentHolder {

    @Inject
    lateinit var presenter: HowToApplyPreferencePresenter

    override var fragment: Fragment? = null

    init {
        isPersistent = false

        DaggerPreferenceComponent.builder()
                .appComponent(AppComponentProvider.provideAppComponent(context))
                .build()
                .inject(this)

        presenter.onTakeView(this)
    }

    override fun onClick() {
        presenter.onClick()
    }

    override fun showDialogHowToApplyUsingPreview() {
        fragment?.fragmentManager?.let {
            HowToApplyUsingPreviewDialogFragment().show(it, "HowToApplyUsingPreviewDialogFragment")
        }
    }

    override fun showDialogHowToApplyUsingChooser() {
        fragment?.fragmentManager?.let {
            HowToApplyUsingChooserDialogFragment().show(it, "HowToApplyUsingChooserDialogFragment")
        }
    }

    override fun showDialogHowToApplyWithoutPreview() {
        fragment?.fragmentManager?.let {
            HowToApplyManuallyDialogFragment().show(it, "HowToApplyManuallyDialogFragment")
        }
    }
}