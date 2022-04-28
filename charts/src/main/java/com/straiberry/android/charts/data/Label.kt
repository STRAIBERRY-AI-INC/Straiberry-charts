package com.straiberry.android.charts.data

import android.graphics.drawable.Drawable

data class Label(
    var drawable: Drawable?=null,
    val label: String,
    var screenPositionX: Float,
    var screenPositionY: Float,
    var drawableScreenPositionX: Float,
    var drawableScreenPositionY: Float,
    var screenPositionXDataPoint:Float=0f
)
