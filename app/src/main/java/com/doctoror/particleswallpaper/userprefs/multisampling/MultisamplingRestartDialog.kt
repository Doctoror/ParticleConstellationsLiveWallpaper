package com.doctoror.particleswallpaper.userprefs.multisampling

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.config.app.ApiLevelProvider
import com.doctoror.particleswallpaper.domain.interactor.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.domain.interactor.OpenChangeWallpaperIntentUseCase
import com.doctoror.particleswallpaper.app.ApplicationlessInjection
import com.doctoror.particleswallpaper.app.actions.FragmentStartActivityForResultAction
import javax.inject.Inject

class MultisamplingRestartDialog : DialogFragment() {

    @Inject
    lateinit var apiLevelProvider: ApiLevelProvider

    @Inject
    lateinit var intentProvider: OpenChangeWallpaperIntentProvider

    private lateinit var useCase: OpenChangeWallpaperIntentUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        ApplicationlessInjection
                .getInstance(activity!!.applicationContext!!)
                .fragmentInjector
                .inject(this)
        super.onCreate(savedInstanceState)

        useCase = OpenChangeWallpaperIntentUseCase(
                intentProvider,
                FragmentStartActivityForResultAction(this))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog
                .Builder(activity)
                .setMessage(R.string.multisampling_restart_message)

        if (canStartPreview()) {
            builder.setPositiveButton(R.string.Set) { _, _ ->
                startPreview()
            }
            builder.setNegativeButton(R.string.Not_now, null)
        } else {
            builder.setPositiveButton(R.string.Close, null)
        }

        return builder.create()
    }

    private fun canStartPreview() = intentProvider.provideActionIntent() != null

    private fun startPreview() {
        useCase.useCase().subscribe()
    }
}
