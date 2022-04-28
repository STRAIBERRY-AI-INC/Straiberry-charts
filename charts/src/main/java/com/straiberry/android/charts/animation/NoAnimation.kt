package com.straiberry.android.charts.animation

import com.straiberry.android.charts.data.DataPoint

class NoAnimation : ChartAnimation<DataPoint>() {

    override fun animateFrom(
        startPosition: Float,
        entries: List<DataPoint>,
        callback: () -> Unit
    ): ChartAnimation<DataPoint> = this
}
