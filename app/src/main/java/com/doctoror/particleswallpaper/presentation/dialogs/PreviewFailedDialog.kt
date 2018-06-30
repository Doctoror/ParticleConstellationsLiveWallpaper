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
package com.doctoror.particleswallpaper.presentation.dialogs

import android.app.AlertDialog
import android.app.DialogFragment
import android.os.Bundle
import com.doctoror.particleswallpaper.R

class PreviewFailedDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog
            .Builder(activity)
            .setMessage(R.string.preview_problem)
            .setPositiveButton(R.string.Close, null)
            .create()!!
}
