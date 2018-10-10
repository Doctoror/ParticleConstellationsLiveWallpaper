package com.doctoror.particleswallpaper.userprefs.data

import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.subjects.AsyncInitialValueBehaviorSubject

const val PREFERENCES_NAME_OPENGL = "prefs_opengl"
const val KEY_NUM_SAMPLES = "num_samples"
const val KEY_OPTIMIZE_TEXTURES = "optimize_textures"

private const val DEFAULT_NUM_SAMPLES = 4
private const val DEFAULT_OPTIMIZE_TEXTURES = true

class OpenGlSettings(
    private val prefsSource: () -> SharedPreferences
) {

    private val numSamplesSubject =
        AsyncInitialValueBehaviorSubject { numSamples }.toSerialized()

    private val optimizeTexturesSubject =
        AsyncInitialValueBehaviorSubject { optimizeTextures }.toSerialized()

    fun observeNumSamples(): Observable<Int> = numSamplesSubject

    fun observeOptimizeTextures(): Observable<Boolean> = optimizeTexturesSubject

    fun resetToDefaults() {
        numSamples = DEFAULT_NUM_SAMPLES
        optimizeTextures = DEFAULT_OPTIMIZE_TEXTURES
    }

    var numSamples
        get() = prefsSource().getInt(KEY_NUM_SAMPLES, DEFAULT_NUM_SAMPLES)
        set(value) {
            prefsSource().edit().putInt(KEY_NUM_SAMPLES, value).apply()
            numSamplesSubject.onNext(value)
        }

    var optimizeTextures
        get() = prefsSource().getBoolean(KEY_OPTIMIZE_TEXTURES, DEFAULT_OPTIMIZE_TEXTURES)
        set(value) {
            prefsSource().edit().putBoolean(KEY_OPTIMIZE_TEXTURES, value).apply()
            optimizeTexturesSubject.onNext(value)
        }
}
