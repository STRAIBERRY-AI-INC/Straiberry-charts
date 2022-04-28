package com.straiberry.android.charts.renderer.executer

import com.straiberry.android.charts.data.*

class MeasureLineChartPaddings {

    operator fun invoke(
        axisType: AxisType,
        labelsHeight: Float,
        longestLabelWidth: Float,
        labelsPaddingToInnerChart: Float,
        lineThickness: Float,
        pointsDrawableWidth: Int,
        pointsDrawableHeight: Int
    ): Paddings {

        val labelsPaddings =
            measureLabelsPaddingsX(
                axisType,
                labelsHeight,
                labelsPaddingToInnerChart
            ).mergeWith(
                measureLabelsPaddingsY(
                    axisType,
                    labelsHeight,
                    longestLabelWidth,
                    labelsPaddingToInnerChart
                )
            )

        return labelsPaddings.copy(
            left = labelsPaddings.left + lineThickness / 2 + pointsDrawableWidth.toFloat() / 2,
            top = labelsPaddings.top + lineThickness + pointsDrawableHeight.toFloat() / 2,
            right = labelsPaddings.right + lineThickness / 2 + pointsDrawableWidth.toFloat() / 2,
            bottom = labelsPaddings.bottom + lineThickness + pointsDrawableHeight.toFloat() / 2
        )
    }

    private fun measureLabelsPaddingsX(
        axisType: AxisType,
        labelsHeight: Float,
        labelsPaddingToInnerChart: Float
    ): Paddings {
        return Paddings(
            left = 0f,
            top = 0f,
            right = 0f,
            bottom = if (axisType.shouldDisplayAxisX()) labelsHeight + labelsPaddingToInnerChart else 0F
        )
    }

    private fun measureLabelsPaddingsY(
        axisType: AxisType,
        labelsHeight: Float,
        longestLabelWidth: Float,
        labelsPaddingToInnerChart: Float
    ): Paddings {

        return if (!axisType.shouldDisplayAxisY())
            Paddings(0f, 0f, 0f, 0f)
        else
            Paddings(
                left = longestLabelWidth + labelsPaddingToInnerChart,
                top = labelsHeight / 2,
                right = 0F,
                bottom = labelsHeight / 2
            )
    }
}
