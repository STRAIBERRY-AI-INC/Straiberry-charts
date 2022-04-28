package com.straiberry.android.charts.renderer.executer

import com.straiberry.android.charts.Painter
import com.straiberry.android.charts.data.*

class DebugWithLabelsFrame {

    operator fun invoke(
        painter: Painter,
        axisType: AxisType,
        xLabels: List<Label>,
        yLabels: List<Label>,
        labelsSize: Float
    ): List<Frame> {
        val ascent = painter.measureLabelAscent(labelsSize)
        val descent = painter.measureLabelDescent(labelsSize)

        val labelsFrames = mutableListOf<Frame>()

        if (axisType.shouldDisplayAxisX())
            labelsFrames += xLabels.map {
                val labelHalfWidth = painter.measureLabelWidth(0,it.label, labelsSize) / 2
                Frame(
                    left = it.screenPositionX - labelHalfWidth,
                    top = it.screenPositionY + ascent,
                    right = it.screenPositionX + labelHalfWidth,
                    bottom = it.screenPositionY + descent
                )
            }

        if (axisType.shouldDisplayAxisY())
            labelsFrames += yLabels.map {
                val labelHalfWidth = painter.measureLabelWidth(0,it.label, labelsSize) / 2
                Frame(
                    left = it.screenPositionX - labelHalfWidth,
                    top = it.screenPositionY + ascent,
                    right = it.screenPositionX + labelHalfWidth,
                    bottom = it.screenPositionY + descent
                )
            }
        return labelsFrames.toList()
    }
}
