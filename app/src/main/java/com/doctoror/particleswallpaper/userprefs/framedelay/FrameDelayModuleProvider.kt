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
package com.doctoror.particleswallpaper.userprefs.framedelay

import com.doctoror.particleswallpaper.framework.view.DisplayFrameRateProvider
import org.koin.dsl.module.module

private const val PARAM_CONTEXT = 0
private const val PARAM_VIEW = 1

fun provideModuleFrameDelay() = module {

    /*
     * Parameter at index 0 must be a FrameDelayPreferenceView.
     */
    factory {
        FrameDelayPreferencePresenter(
            context = it[PARAM_CONTEXT],
            displayFrameRateProvider = DisplayFrameRateProvider(),
            schedulers = get(),
            settings = get(),
            view = it[PARAM_VIEW]
        )
    }
}
