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
package com.doctoror.particleswallpaper.framework.di.components

import com.doctoror.particleswallpaper.framework.di.scopes.PerPreference
import com.doctoror.particleswallpaper.userprefs.bgcolor.BackgroundColorPreference
import com.doctoror.particleswallpaper.userprefs.bgimage.BackgroundImagePreference
import com.doctoror.particleswallpaper.userprefs.density.DensityPreference
import com.doctoror.particleswallpaper.userprefs.framedelay.FrameDelayPreference
import com.doctoror.particleswallpaper.userprefs.howtoapply.HowToApplyPreference
import com.doctoror.particleswallpaper.userprefs.license.LicensePreference
import com.doctoror.particleswallpaper.userprefs.linelength.LineLengthPreference
import com.doctoror.particleswallpaper.userprefs.linescale.LineScalePreference
import com.doctoror.particleswallpaper.userprefs.multisampling.MultisamplingPreference
import com.doctoror.particleswallpaper.userprefs.optimizetextures.OptimizeTexturesPreference
import com.doctoror.particleswallpaper.userprefs.particlecolor.ParticlesColorPreference
import com.doctoror.particleswallpaper.userprefs.particlescale.ParticleScalePreference
import com.doctoror.particleswallpaper.userprefs.performancetips.PerformanceTipsPreference
import com.doctoror.particleswallpaper.userprefs.preview.PreviewPreference
import com.doctoror.particleswallpaper.userprefs.resettodefaults.ResetToDefaultsPreference
import com.doctoror.particleswallpaper.userprefs.speedfactor.SpeedFactorPreference
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
    fun inject(p: FrameDelayPreference)
    fun inject(p: HowToApplyPreference)
    fun inject(p: LicensePreference)
    fun inject(p: LineLengthPreference)
    fun inject(p: LineScalePreference)
    fun inject(p: DensityPreference)
    fun inject(p: MultisamplingPreference)
    fun inject(p: OptimizeTexturesPreference)
    fun inject(p: ParticleScalePreference)
    fun inject(p: ParticlesColorPreference)
    fun inject(p: PerformanceTipsPreference)
    fun inject(p: PreviewPreference)
    fun inject(p: ResetToDefaultsPreference)
    fun inject(p: SpeedFactorPreference)
}
