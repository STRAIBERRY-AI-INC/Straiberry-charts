package com.straiberry.android.charts.renderer.executer

import com.straiberry.android.charts.data.DataPoint
import com.straiberry.android.charts.data.Frame

class GetHorizontalBarBackgroundFrames {

    operator fun invoke(
        innerFrame: Frame,
        spacingBetweenBars: Float,
        data: List<DataPoint>
    ): List<Frame> {
        val halfBarWidth =
            (innerFrame.bottom - innerFrame.top - (data.size + 1) * spacingBetweenBars) /
                data.size / 2

        return data.map {
            Frame(
                left = innerFrame.left,
                top = it.screenPositionY - halfBarWidth,
                right = innerFrame.right,
                bottom = it.screenPositionY + halfBarWidth
            )
        }
    }
}
