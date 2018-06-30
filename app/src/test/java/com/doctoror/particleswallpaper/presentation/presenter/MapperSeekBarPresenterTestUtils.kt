package com.doctoror.particleswallpaper.presentation.presenter

import org.junit.jupiter.api.Assertions.assertEquals

fun <T> testMapper(p: MapperSeekBarPresenter<T>) {
    testMapperMinValue(p)
    testMapperMaxValue(p)
}

private fun <T> testMapperMinValue(p: MapperSeekBarPresenter<T>) {
    val seekBarValue = 0
    val frameDelay = p.transformToRealValue(seekBarValue)
    assertEquals(seekBarValue, p.transformToProgress(frameDelay))
}

private fun <T> testMapperMaxValue(p: MapperSeekBarPresenter<T>) {
    val seekBarValue = p.getSeekbarMax()
    val frameDelay = p.transformToRealValue(seekBarValue)
    assertEquals(seekBarValue, p.transformToProgress(frameDelay))
}
