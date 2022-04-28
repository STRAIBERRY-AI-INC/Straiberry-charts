package com.straiberry.android.charts

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import com.straiberry.android.charts.data.Label

interface Labels {
    fun draw(canvas: Canvas, paint: Paint, xLabels: List<Label>, @ColorInt labelColor: Int)
}
