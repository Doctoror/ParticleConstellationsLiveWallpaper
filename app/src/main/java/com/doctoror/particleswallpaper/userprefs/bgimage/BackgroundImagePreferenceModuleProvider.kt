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

import com.doctoror.particleswallpaper.framework.file.BackgroundImageManager
import com.doctoror.particleswallpaper.framework.file.BackgroundImageManagerImpl
import com.doctoror.particleswallpaper.framework.file.FileSaver
import com.doctoror.particleswallpaper.framework.file.FileUriResolver
import org.koin.dsl.module.module

private const val PARAM_VIEW = 0

class BackgroundImagePreferenceModuleProvider {

    /**
     * Parameter at 0 should be BackgroundColorPreferenceView.
     */
    fun provide() = module {

        factory<BackgroundImageManager> {
            BackgroundImageManagerImpl(
                context = get(),
                fileSaver = FileSaver(get()),
                fileUriResolver = FileUriResolver(get())
            )
        }

        factory {
            PickImageGetContentUseCase()
        }

        factory {
            PickImageDocumentUseCase(get())
        }

        factory {
            BackgroundImagePreferencePresenter(
                apiLevelProvider = get(),
                backgroundImageManager = get(),
                context = get(),
                defaults = get(),
                glide = get(),
                pickImageGetContentUseCase = get(),
                pickImageDocumentUseCase = get(),
                schedulers = get(),
                settings = get(),
                view = it[PARAM_VIEW]
            )
        }
    }
}
