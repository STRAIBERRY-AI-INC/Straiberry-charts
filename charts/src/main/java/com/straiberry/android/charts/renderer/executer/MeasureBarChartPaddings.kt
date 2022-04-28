package com.straiberry.android.charts.renderer.executer

import com.straiberry.android.charts.data.*

class MeasureBarChartPaddings {

    operator fun invoke(
        axisType: AxisType,
        labelsHeight: Float,
        longestLabelWidth: Float,
        labelsPaddingToInnerChart: Float
    ): Paddings {
        return measurePaddingsX(axisType, labelsHeight, labelsPaddingToInnerChart)
            .mergeWith(
                measurePaddingsY(axisType, labelsHeight, longestLabelWidth, labelsPaddingToInnerChart)
            )
    }

    private fun measurePaddingsX(
        axisType: AxisType,
        labelsHeight: Float,
        labelsPaddingToInnerChart: Float
    ): Paddings {
        return Paddings(
            left = 0F,
            top = 0F,
            right = 0f,
            bottom = if (axisType.shouldDisplayAxisX()) labelsHeight + labelsPaddingToInnerChart else 0F
        )
    }

    private fun measurePaddingsY(
        axisType: AxisType,
        labelsHeight: Float,
        longestLabelWidth: Float,
        labelsPaddingToInnerChart: Float
    ): Paddings {

        if (!axisType.shouldDisplayAxisY())
            return Paddings(0F, 0F, 0F, 0F)

        return Paddings(
            left = longestLabelWidth + labelsPaddingToInnerChart,
            top = labelsHeight / 2,
            right = 0F,
            bottom = labelsHeight / 2
        )
    }
}
