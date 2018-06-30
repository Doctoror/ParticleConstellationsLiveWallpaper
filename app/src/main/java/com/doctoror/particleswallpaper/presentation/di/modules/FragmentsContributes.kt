package com.doctoror.particleswallpaper.presentation.di.modules

import com.doctoror.particleswallpaper.presentation.config.ConfigFragment
import com.doctoror.particleswallpaper.presentation.config.ConfigFragmentLollipop
import com.doctoror.particleswallpaper.presentation.dialogs.HowToApplyUsingChooserDialogFragment
import com.doctoror.particleswallpaper.presentation.dialogs.HowToApplyUsingPreviewDialogFragment
import com.doctoror.particleswallpaper.presentation.dialogs.MultisamplingRestartDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentsContributes {

    @ContributesAndroidInjector
    fun configFragment(): ConfigFragment

    @ContributesAndroidInjector
    fun configFragmentLollipop(): ConfigFragmentLollipop

    @ContributesAndroidInjector
    fun howToApplyUsingChooserDialogFragment(): HowToApplyUsingChooserDialogFragment

    @ContributesAndroidInjector
    fun howToApplyUsingPreviewDialogFragment(): HowToApplyUsingPreviewDialogFragment

    @ContributesAndroidInjector
    fun multisamplingRestartDialog(): MultisamplingRestartDialog
}
