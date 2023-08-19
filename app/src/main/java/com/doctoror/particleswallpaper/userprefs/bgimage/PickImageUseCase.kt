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

import android.content.Context
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.doctoror.particleswallpaper.app.REQUEST_CODE_PICK_IMAGE
import com.doctoror.particleswallpaper.framework.app.actions.StartActivityForResultAction

class PickImageUseCase(private val contract: ActivityResultContracts.PickVisualMedia) {

    fun invoke(
        context: Context,
        startActivityForResultAction: StartActivityForResultAction
    ) {
        val intent = contract.createIntent(
            context,
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )

        startActivityForResultAction.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }
}
