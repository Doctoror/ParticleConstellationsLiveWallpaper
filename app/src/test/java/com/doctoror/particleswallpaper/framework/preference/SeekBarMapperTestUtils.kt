package com.doctoror.particleswallpaper.framework.preference

import org.junit.Assert.assertEquals

fun <T> testMapper(p: SeekBarMapper<T>) {
    testMapperMinValue(p)
    testMapperMaxValue(p)
}

private fun <T> testMapperMinValue(p: SeekBarMapper<T>) {
    val seekBarValue = 0
    val realValue = p.transformToRealValue(seekBarValue)
    assertEquals(seekBarValue, p.transformToProgress(realValue))
}

private fun <T> testMapperMaxValue(p: SeekBarMapper<T>) {
    val seekBarValue = p.getSeekbarMax()
    val realValue = p.transformToRealValue(seekBarValue)
    assertEquals(seekBarValue, p.transformToProgress(realValue))
}
