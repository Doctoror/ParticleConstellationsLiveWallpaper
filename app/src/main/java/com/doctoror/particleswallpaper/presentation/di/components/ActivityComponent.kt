/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.presentation.di.components

import com.doctoror.particleswallpaper.presentation.config.ConfigActivity
import com.doctoror.particleswallpaper.presentation.config.ConfigActivityLollipop
import com.doctoror.particleswallpaper.presentation.di.modules.ActivityModule
import com.doctoror.particleswallpaper.presentation.di.scopes.PerActivity
import dagger.Component

/**
 * Created by Yaroslav Mytkalyk on 14.06.17.
 */
@PerActivity
@Component(
        dependencies = arrayOf(ConfigComponent::class),
        modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(t: ConfigActivity)
}