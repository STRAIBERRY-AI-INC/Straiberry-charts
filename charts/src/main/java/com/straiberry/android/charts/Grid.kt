package com.straiberry.android.charts

import android.graphics.Canvas
import com.straiberry.android.charts.data.Frame

interface Grid {
    fun draw(
        canvas: Canvas,
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    )
}
