package com.doctoror.particleswallpaper.userprefs.multisampling

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.framework.app.actions.FragmentStartActivityForResultAction
import com.doctoror.particleswallpaper.framework.di.inject
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentProvider
import com.doctoror.particleswallpaper.userprefs.preview.OpenChangeWallpaperIntentUseCase

class MultisamplingRestartDialog : DialogFragment() {

    private val intentProvider: OpenChangeWallpaperIntentProvider by inject()

    private lateinit var useCase: OpenChangeWallpaperIntentUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        useCase = OpenChangeWallpaperIntentUseCase(
            intentProvider,
            FragmentStartActivityForResultAction(this)
        )
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
        useCase.action().subscribe()
    }
}
