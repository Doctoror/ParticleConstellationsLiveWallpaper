package com.doctoror.particleswallpaper.presentation.config

import android.os.Bundle
import com.doctoror.particleswallpaper.R

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * Lollipop implementation for config preference fragment.
 * Removes "Preview" preference since we have a Toolbar ActionBar with the desired action in Lollipop.
 */
class ConfigFragmentLollipop: ConfigFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val p = findPreference(getString(R.string.pref_key_preview))
        if (p != null) {
            preferenceScreen?.removePreference(p)
        }
    }

}