package com.straiberry.android.charts.renderer.executer


import com.straiberry.android.charts.data.DataPoint
import com.straiberry.android.charts.data.Frame

class GetVerticalBarBackgroundFrames {

    operator fun invoke(
        innerFrame: Frame,
        spacingBetweenBars: Float,
        data: List<DataPoint>
    ): List<Frame> {
        val halfBarWidth =
            (innerFrame.right - innerFrame.left - (data.size + 1) * spacingBetweenBars) /
                data.size / 2

        return data.map {
            Frame(
                left = it.screenPositionX - halfBarWidth,
                top = innerFrame.top,
                right = it.screenPositionX + halfBarWidth,
                bottom = innerFrame.bottom
            )
        }
    }
}
