package com.doctoror.particleswallpaper.userprefs.bgcolor

import org.koin.dsl.module.module

private const val PARAM_VIEW = 0

class BackgroundColorPreferenceModuleProvider {

    /**
     * Parameter at 0 should be BackgroundColorPreferenceView.
     */
    fun provide() = module {
        factory {
            BackgroundColorPreferencePresenter(
                schedulers = get(),
                settings = get(),
                defaults = get(),
                view = it[PARAM_VIEW]
            )
        }
    }
}
