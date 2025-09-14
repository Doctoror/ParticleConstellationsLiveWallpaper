package com.doctoror.particleswallpaper.userprefs.particlesscroll

import android.content.Context
import android.preference.CheckBoxPreference
import android.util.AttributeSet
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.doctoror.particleswallpaper.framework.di.inject
import org.koin.core.parameter.parametersOf

class ParticlesScrollPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0 // overridden to not be passed
) : CheckBoxPreference(context, attrs),
    ParticlesScrollPreferenceView,
    DefaultLifecycleObserver {

    private val presenter: ParticlesScrollPreferencePresenter by inject(
        context = context,
        parameters = { parametersOf(this as ParticlesScrollPreferenceView) }
    )

    init {
        onPreferenceChangeListener = OnPreferenceChangeListener { _, v ->
            presenter.onPreferenceChange(v as Boolean)
            true
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        presenter.onStart()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        presenter.onStop()
    }
}
