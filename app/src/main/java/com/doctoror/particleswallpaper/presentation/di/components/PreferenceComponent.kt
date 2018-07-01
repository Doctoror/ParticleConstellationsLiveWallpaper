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

import com.doctoror.particleswallpaper.presentation.di.scopes.PerPreference
import com.doctoror.particleswallpaper.presentation.preference.*
import dagger.Component

/**
 * Created by Yaroslav Mytkalyk on 14.06.17.
 *
 * Per Preference component
 */
@PerPreference
@Component(dependencies = [AppComponent::class])
interface PreferenceComponent {

    fun inject(p: BackgroundColorPreference)
    fun inject(p: BackgroundImagePreference)
    fun inject(p: DotScalePreference)
    fun inject(p: FrameDelayPreference)
    fun inject(p: HowToApplyPreference)
    fun inject(p: LicensePreference)
    fun inject(p: LineDistancePreference)
    fun inject(p: LineScalePreference)
    fun inject(p: NumDotsPreference)
    fun inject(p: NumSamplesPreference)
    fun inject(p: OptimizeTexturesPreference)
    fun inject(p: ParticlesColorPreference)
    fun inject(p: PerformanceTipsPreference)
    fun inject(p: PreviewPreference)
    fun inject(p: ResetToDefaultsPreference)
    fun inject(p: SpeedFactorPreference)
}
