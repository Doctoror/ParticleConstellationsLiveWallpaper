package com.doctoror.particleswallpaper.presentation.di.modules

import com.doctoror.particleswallpaper.userprefs.ConfigFragment
import com.doctoror.particleswallpaper.userprefs.ConfigFragmentLollipop
import com.doctoror.particleswallpaper.userprefs.howtoapply.HowToApplyUsingChooserDialogFragment
import com.doctoror.particleswallpaper.userprefs.howtoapply.HowToApplyUsingPreviewDialogFragment
import com.doctoror.particleswallpaper.userprefs.multisampling.MultisamplingRestartDialog
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
