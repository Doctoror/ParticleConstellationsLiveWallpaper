package com.doctoror.particleswallpaper.userprefs.data

import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.subjects.AsyncInitialValueBehaviorSubject

const val PREFERENCES_NAME_OPENGL = "prefs_opengl"
const val KEY_NUM_SAMPLES = "num_samples"

private const val DEFAULT_NUM_SAMPLES = 4

class OpenGlSettings(
    private val prefsSource: () -> SharedPreferences
) {

    private val numSamplesSubject =
        AsyncInitialValueBehaviorSubject { numSamples }.toSerialized()

    fun observeNumSamples(): Observable<Int> = numSamplesSubject

    fun resetToDefaults() {
        numSamples = DEFAULT_NUM_SAMPLES
    }

    var numSamples
        get() = prefsSource().getInt(KEY_NUM_SAMPLES, DEFAULT_NUM_SAMPLES)
        set(value) {
            prefsSource().edit().putInt(KEY_NUM_SAMPLES, value).apply()
            numSamplesSubject.onNext(value)
        }
}
