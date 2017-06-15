/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.data.config

import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import com.doctoror.particlesdrawable.ParticlesSceneConfiguration

/**
 * Created by Yaroslav Mytkalyk on 15.06.17.
 */
class MutableParticlesScene : ParticlesSceneConfiguration {

    private var mMinDotRadius = 1f
    private var mMaxDotRadius = 1f

    private var mLineThickness = 1f

    private var mLineDistance = 1f

    private var mNumDots = 1

    @ColorInt
    private var mDotColor = 0

    @ColorInt
    private var mLineColor = 0

    private var mDelay = 10

    private var mStepMultiplier = 1f

    /**
     * {@inheritDoc}
     */
    override fun setFrameDelay(@IntRange(from = 0) delay: Int) {
        mDelay = delay
    }

    /**
     * {@inheritDoc}
     */
    override fun getFrameDelay(): Int {
        return mDelay
    }

    /**
     * {@inheritDoc}
     */
    override fun setStepMultiplier(@FloatRange(from = 0.0) stepMultiplier: Float) {
        mStepMultiplier = stepMultiplier
    }

    /**
     * {@inheritDoc}
     */
    override fun getStepMultiplier(): Float {
        return mStepMultiplier
    }

    /**
     * {@inheritDoc}
     */
    override fun setDotRadiusRange(
            @FloatRange(from = 0.5) minRadius: Float,
            @FloatRange(from = 0.5) maxRadius: Float) {
        mMinDotRadius = minRadius
        mMaxDotRadius = maxRadius
    }

    /**
     * {@inheritDoc}
     */
    override fun getMinDotRadius(): Float {
        return mMinDotRadius
    }

    /**
     * {@inheritDoc}
     */
    override fun getMaxDotRadius(): Float {
        return mMaxDotRadius
    }

    /**
     * {@inheritDoc}
     */
    override fun setLineThickness(@FloatRange(from = 1.0) lineThickness: Float) {
        mLineThickness = lineThickness
    }

    /**
     * {@inheritDoc}
     */
    override fun getLineThickness(): Float {
        return mLineThickness
    }

    /**
     * {@inheritDoc}
     */
    override fun setLineDistance(@FloatRange(from = 0.0) lineDistance: Float) {
        mLineDistance = lineDistance
    }

    /**
     * {@inheritDoc}
     */
    override fun getLineDistance(): Float {
        return mLineDistance
    }

    /**
     * {@inheritDoc}
     */
    override fun setNumDots(@IntRange(from = 0) newNum: Int) {
        mNumDots = newNum
    }

    /**
     * {@inheritDoc}
     */
    override fun getNumDots(): Int {
        return mNumDots
    }

    /**
     * {@inheritDoc}
     */
    override fun setDotColor(@ColorInt dotColor: Int) {
        mDotColor = dotColor
    }

    /**
     * {@inheritDoc}
     */
    override fun getDotColor(): Int {
        return mDotColor
    }

    /**
     * {@inheritDoc}
     */
    override fun setLineColor(@ColorInt lineColor: Int) {
        mLineColor = lineColor
    }

    /**
     * {@inheritDoc}
     */
    override fun getLineColor(): Int {
        return mLineColor
    }

}