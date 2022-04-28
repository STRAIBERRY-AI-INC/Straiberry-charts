package com.straiberry.android.charts.configuration

import com.straiberry.android.charts.data.AxisType
import com.straiberry.android.charts.data.Frame
import com.straiberry.android.charts.data.Paddings
import com.straiberry.android.charts.data.Scale

open class ChartConfiguration(
    open val width: Int,
    open val height: Int,
    open val paddings: Paddings,
    open val axis: AxisType,
    open val labelsSize: Float,
    open val scale: Scale,
    open val labelsFormatter: (Int) -> String
)

internal fun ChartConfiguration.toOuterFrame(): Frame {
    return Frame(
        left = paddings.left,
        top = paddings.top,
        right = width - paddings.right,
        bottom = height - paddings.bottom
    )
}
