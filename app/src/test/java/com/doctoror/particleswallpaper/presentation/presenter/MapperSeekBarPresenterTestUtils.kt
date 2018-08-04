package com.doctoror.particleswallpaper.presentation.presenter

import org.junit.jupiter.api.Assertions.assertEquals

fun <T> testMapper(p: MapperSeekBarPresenter<T>) {
    testMapperMinValue(p)
    testMapperMaxValue(p)
}

private fun <T> testMapperMinValue(p: MapperSeekBarPresenter<T>) {
    val seekBarValue = 0
    val realValue = p.transformToRealValue(seekBarValue)
    assertEquals(seekBarValue, p.transformToProgress(realValue))
}

private fun <T> testMapperMaxValue(p: MapperSeekBarPresenter<T>) {
    val seekBarValue = p.getSeekbarMax()
    val realValue = p.transformToRealValue(seekBarValue)
    assertEquals(seekBarValue, p.transformToProgress(realValue))
}
