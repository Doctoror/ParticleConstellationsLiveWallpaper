package com.doctoror.particleswallpaper.userprefs

import android.os.Bundle
import com.doctoror.particleswallpaper.R

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * Lollipop implementation for config preference fragment.
 * Removes "Preview" preference since we have a Toolbar ActionBar with the desired action in Lollipop.
 */
class ConfigFragmentLollipop : ConfigFragment() {

    @Deprecated("Must declare as deprecated when overriding deprecated api")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val p = findPreference(getString(R.string.pref_key_apply))
        if (p != null) {
            preferenceScreen?.removePreference(p)
        }
    }
}
