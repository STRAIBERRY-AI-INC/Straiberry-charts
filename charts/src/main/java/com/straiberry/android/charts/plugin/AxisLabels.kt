package com.straiberry.android.charts.plugin

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.ColorInt
import com.straiberry.android.charts.Labels
import com.straiberry.android.charts.data.Label
import com.straiberry.android.charts.extenstions.centerAt
import com.straiberry.android.common.extensions.dp

private val DefaultLabelSizeForHorizontalChart = (8F).dp
class AxisLabels : Labels {
    override fun draw(
        canvas: Canvas,
        paint: Paint,
        xLabels: List<Label>,
        @ColorInt labelColor: Int
    ) {
        xLabels.forEach {
            when {
                // If size of label is 12 then make text align to left
                xLabels.size == 12 -> {
                    canvas.drawText(
                        it.label,
                        it.screenPositionX,
                        it.screenPositionY,
                        paint.apply {
                            color = labelColor
                            textAlign = Paint.Align.LEFT
                        }
                    )
                }
                // Draw drawables for horizontal bar chart if its not null
                it.drawable!=null -> {
                    it.drawable?.apply {
                        centerAt(it.drawableScreenPositionX, it.drawableScreenPositionY)
                        draw(canvas)
                    }
                    // Draw label for horizontal chart
                    canvas.drawText(
                        it.label,
                        it.screenPositionX,
                        it.screenPositionY,
                        paint.apply {
                            color = labelColor
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            textAlign = Paint.Align.LEFT
                            textSize = DefaultLabelSizeForHorizontalChart
                        }
                    )
                }
                else -> {
                    canvas.drawText(
                        it.label,
                        it.screenPositionX,
                        it.screenPositionY,
                        paint.apply {
                            color = labelColor
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}
