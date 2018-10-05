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

import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import com.doctoror.particleswallpaper.app.REQUEST_CODE_OPEN_DOCUMENT
import com.doctoror.particleswallpaper.domain.interactor.StartActivityForResultAction
import javax.inject.Inject

class PickImageDocumentUseCase @Inject constructor(
        private val getContentUseCase: PickImageGetContentUseCase) {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun invoke(startActivityForResultAction: StartActivityForResultAction) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.type = "image/*"
        try {
            startActivityForResultAction.startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT)
        } catch (e: ActivityNotFoundException) {
            getContentUseCase.invoke(startActivityForResultAction)
        }
    }
}
