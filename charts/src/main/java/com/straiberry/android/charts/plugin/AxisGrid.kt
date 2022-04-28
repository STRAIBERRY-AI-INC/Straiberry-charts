package com.straiberry.android.charts.plugin

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.straiberry.android.charts.Grid
import com.straiberry.android.charts.data.Frame

private const val DefaultStrokeWidth = 3f
private const val DefaultDashInterval=4
class AxisGrid : Grid {

    var gridType = GridType.FULL
    var gridEffect = GridEffect.SOLID
    var colorY = -0x1000000
    var colorX = -0x1000000
    var color = -0x1000000
    var strokeWidth = DefaultStrokeWidth

    private val paintX by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            Paint.ANTI_ALIAS_FLAG
            color = this@AxisGrid.colorX
            strokeWidth = this@AxisGrid.strokeWidth

            if (gridEffect != GridEffect.SOLID) {
                val interval =
                    if (gridEffect == GridEffect.DASHED) DefaultStrokeWidth * DefaultDashInterval
                    else DefaultStrokeWidth
                pathEffect =
                    DashPathEffect(
                        floatArrayOf(interval, interval),
                        0f
                    )
            }
        }
    }

    private val paintY by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = this@AxisGrid.colorY
            strokeWidth = this@AxisGrid.strokeWidth

            if (gridEffect != GridEffect.SOLID) {
                val interval =
                    if (gridEffect == GridEffect.DASHED) DefaultStrokeWidth * DefaultDashInterval
                    else DefaultStrokeWidth
                pathEffect =
                    DashPathEffect(
                        floatArrayOf(interval, interval),
                        0f
                    )
            }
        }
    }

    override fun draw(
        canvas: Canvas,
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    ) {
        if (gridType == GridType.FULL || gridType == GridType.VERTICAL) {
            xLabelsPositions.forEach {
                canvas.drawLine(it, innerFrame.bottom, it, innerFrame.top, paintX)
            }
        }

        if (gridType == GridType.FULL || gridType == GridType.HORIZONTAL) {
            yLabelsPositions.forEach {
                canvas.drawLine(innerFrame.left, it, innerFrame.right, it, paintY)
            }
        }
    }
}

enum class GridType {
    FULL,
    VERTICAL,
    HORIZONTAL
}

enum class GridEffect {
    SOLID,
    DASHED,
    DOTTED
}
