/*
 * Copyright (C) 2019 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.userprefs.engine

import android.app.AlertDialog
import android.app.DialogFragment
import android.os.Bundle
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.di.get
import com.doctoror.particleswallpaper.framework.util.OpenGlEnabledStateChanger

private const val ARG_SHOULD_ENABLE_OPENGL = "ARG_SHOULD_ENABLE_OPENGL"

fun newEngineRestartDialog(shouldEnableOpenGl: Boolean) = EngineRestartDialog().apply {
    arguments = Bundle().apply {
        putBoolean(ARG_SHOULD_ENABLE_OPENGL, shouldEnableOpenGl)
    }
}

class EngineRestartDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog
        .Builder(activity)
        .setMessage(R.string.engine_restart_message)
        .setPositiveButton(R.string.Set) { _, _ -> applySettings() }
        .setNegativeButton(R.string.Cancel, null)
        .create()!!

    private fun applySettings() {
        activity?.let { activity ->
            val openGlEnabledStateChanger: OpenGlEnabledStateChanger = get(context = activity)
            openGlEnabledStateChanger.setOpenglGlEnabled(
                openGlEnabled = obtainShouldEnableOpenGl(),
                shouldKillApp = true
            )
        }
    }

    private fun obtainShouldEnableOpenGl() = arguments!!.getBoolean(ARG_SHOULD_ENABLE_OPENGL)
}
