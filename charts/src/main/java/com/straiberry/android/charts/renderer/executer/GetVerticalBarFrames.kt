package com.straiberry.android.charts.renderer.executer

import com.straiberry.android.charts.data.DataPoint
import com.straiberry.android.charts.data.Frame

class GetVerticalBarFrames {

    operator fun invoke(
        innerFrame: Frame,
        spacingBetweenBars: Float,
        data: List<DataPoint>
    ): List<Frame> {
        val halfBarWidth =
            (innerFrame.right - innerFrame.left - (data.size + 1) * spacingBetweenBars) /
                    data.size / 2

        return data.map {
            // Draw a zero-height frame when data is empty
            if (it.value == -1f)
                Frame()
            else
                Frame(
                    left = it.screenPositionX - halfBarWidth,
                    top = it.screenPositionY,
                    right = it.screenPositionX + halfBarWidth,
                    bottom = innerFrame.bottom,
                    y = it.drawableScreenPositionY
                )

        }
    }
}
