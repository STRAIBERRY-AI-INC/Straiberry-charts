package com.straiberry.android.charts.extenstions

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

fun Canvas.drawChartBar(bar: RectF, radius: Float, paint: Paint) {
    drawRoundRect(bar, radius, radius, paint)
}
