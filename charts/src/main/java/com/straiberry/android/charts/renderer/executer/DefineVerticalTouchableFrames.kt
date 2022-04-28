package com.straiberry.android.charts.renderer.executer

import com.straiberry.android.charts.data.Frame

class DefineVerticalTouchableFrames {

    operator fun invoke(
        innerFrame: Frame,
        datapointsCoordinates: List<Pair<Float, Float>>
    ): List<Frame> {

        val halfDistanceBetweenDataPoints =
            (innerFrame.right - innerFrame.left - (datapointsCoordinates.size + 1)) /
                datapointsCoordinates.size / 2

        return datapointsCoordinates.map {
            Frame(
                left = it.first - halfDistanceBetweenDataPoints,
                top = innerFrame.top,
                right = it.first + halfDistanceBetweenDataPoints,
                bottom = innerFrame.bottom
            )
        }
    }
}
