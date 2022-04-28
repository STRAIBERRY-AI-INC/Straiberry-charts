package com.straiberry.android.charts.configuration

import com.straiberry.android.charts.data.AxisType
import com.straiberry.android.charts.data.Paddings
import com.straiberry.android.charts.data.Scale

data class LineChartConfiguration(
    override val width: Int,
    override val height: Int,
    override val paddings: Paddings,
    override val axis: AxisType,
    override val labelsSize: Float,
    override val scale: Scale,
    override val labelsFormatter: (Int) -> String = { it.toString() },
    val lineThickness: Float,
    val pointsDrawableWidth: Int,
    val pointsDrawableHeight: Int,
    val fillColor: Int,
    val gradientFillColors: IntArray,
    val clickableRadius: Int
) : ChartConfiguration(
    width = width,
    height = height,
    paddings = paddings,
    axis = axis,
    labelsSize = labelsSize,
    scale = scale,
    labelsFormatter = labelsFormatter
)
