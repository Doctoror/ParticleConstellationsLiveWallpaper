package com.doctoror.particleswallpaper.userprefs.particlesscroll

import android.content.Context
import android.preference.CheckBoxPreference
import android.util.AttributeSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.doctoror.particleswallpaper.framework.di.inject
import org.koin.core.parameter.parametersOf

class ParticlesScrollPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @Suppress("UNUSED_PARAMETER") defStyle: Int = 0 // overridden to not be passed
) : CheckBoxPreference(context, attrs),
    ParticlesScrollPreferenceView,
    LifecycleObserver {

    private val presenter: ParticlesScrollPreferencePresenter by inject(
        context = context,
        parameters = { parametersOf(this as ParticlesScrollPreferenceView) }
    )

    init {
        setOnPreferenceChangeListener { _, v ->
            presenter.onPreferenceChange(v as Boolean)
            true
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        presenter.onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        presenter.onStop()
    }
}
