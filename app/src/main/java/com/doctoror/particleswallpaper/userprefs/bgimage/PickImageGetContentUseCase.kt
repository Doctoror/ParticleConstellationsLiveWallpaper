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
package com.doctoror.particleswallpaper.userprefs.bgimage

import android.content.ActivityNotFoundException
import android.content.Intent
import com.doctoror.particleswallpaper.app.REQUEST_CODE_GET_CONTENT
import com.doctoror.particleswallpaper.domain.interactor.StartActivityForResultAction
import javax.inject.Inject

class PickImageGetContentUseCase @Inject constructor() {

    @Throws(ActivityNotFoundException::class)
    fun invoke(startActivityForResultAction: StartActivityForResultAction) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResultAction.startActivityForResult(
                    Intent.createChooser(intent, null), REQUEST_CODE_GET_CONTENT)
        } catch (e: ActivityNotFoundException) {
            startActivityForResultAction.startActivityForResult(intent, REQUEST_CODE_GET_CONTENT)
        }
    }
}
