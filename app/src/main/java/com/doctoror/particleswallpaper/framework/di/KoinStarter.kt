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
package com.doctoror.particleswallpaper.framework.di

import android.content.Context
import com.doctoror.particleswallpaper.engine.provideModuleEngine
import com.doctoror.particleswallpaper.userprefs.bgcolor.provideModuleBackgroundColor
import com.doctoror.particleswallpaper.userprefs.bgimage.provideModuleBackgroundImage
import com.doctoror.particleswallpaper.userprefs.bgscroll.provideModuleBackgroundScroll
import com.doctoror.particleswallpaper.userprefs.data.provideModuleSettings
import com.doctoror.particleswallpaper.userprefs.density.provideModuleDensity
import com.doctoror.particleswallpaper.userprefs.engine.provideModuleEnginePreference
import com.doctoror.particleswallpaper.userprefs.enginetips.provideModuleEngineTips
import com.doctoror.particleswallpaper.userprefs.framedelay.provideModuleFrameDelay
import com.doctoror.particleswallpaper.userprefs.howtoapply.provideModuleHowToApply
import com.doctoror.particleswallpaper.userprefs.license.provideModuleLicense
import com.doctoror.particleswallpaper.userprefs.linelength.provideModuleLineLength
import com.doctoror.particleswallpaper.userprefs.linescale.provideModuleLineScale
import com.doctoror.particleswallpaper.userprefs.multisampling.provideModuleMultisampling
import com.doctoror.particleswallpaper.userprefs.particlecolor.provideModuleParticleColor
import com.doctoror.particleswallpaper.userprefs.particlescale.provideModuleParticleScale
import com.doctoror.particleswallpaper.userprefs.particlesscroll.provideModuleParticlesScroll
import com.doctoror.particleswallpaper.userprefs.performancetips.provideModulePerformanceTips
import com.doctoror.particleswallpaper.userprefs.preview.provideModulePreview
import com.doctoror.particleswallpaper.userprefs.provideModuleConfigActivity
import com.doctoror.particleswallpaper.userprefs.resettodefaults.provideModuleResetToDefaults
import com.doctoror.particleswallpaper.userprefs.speedfactor.provideModuleSpeedFactor
import org.koin.standalone.StandAloneContext

class KoinStarter {

    fun startKoin(context: Context) {
        StandAloneContext.startKoin(
            listOf(
                provideModuleApp(context),
                provideModuleBackgroundColor(),
                provideModuleBackgroundImage(),
                provideModuleBackgroundScroll(),
                provideModuleConfigActivity(),
                provideModuleDensity(),
                provideModuleEngine(),
                provideModuleEnginePreference(),
                provideModuleEngineTips(),
                provideModuleFrameDelay(),
                provideModuleHowToApply(),
                provideModuleLicense(),
                provideModuleLineLength(),
                provideModuleLineScale(),
                provideModuleMultisampling(),
                provideModuleParticleColor(),
                provideModuleParticleScale(),
                provideModuleParticlesScroll(),
                provideModulePerformanceTips(),
                provideModulePreview(),
                provideModuleResetToDefaults(),
                provideModuleSpeedFactor(),
                provideModuleSettings()
            )
        )
    }
}
