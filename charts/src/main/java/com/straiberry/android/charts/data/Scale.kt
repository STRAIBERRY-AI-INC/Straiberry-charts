package com.straiberry.android.charts.data

import com.straiberry.android.charts.renderer.RendererConstants


data class Scale(val min: Float, val max: Float) {
    val size = max - min
}

fun Scale.notInitialized() = max == min && min == RendererConstants.NotInitialized
