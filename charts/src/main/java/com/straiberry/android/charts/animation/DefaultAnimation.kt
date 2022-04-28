package com.straiberry.android.charts.animation

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.straiberry.android.charts.data.DataPoint

class DefaultAnimation : ChartAnimation<DataPoint>() {

    override fun animateFrom(
        startPosition: Float,
        entries: List<DataPoint>,
        callback: () -> Unit
    ): ChartAnimation<DataPoint> {

        // Entries animators
        entries.forEach { dataPoint ->
            val eAnimator: ObjectAnimator =
                ObjectAnimator.ofFloat(
                    dataPoint,
                    "screenPositionY",
                    startPosition,
                    dataPoint.screenPositionY
                )
            eAnimator.duration = duration
            eAnimator.interpolator = interpolator
            eAnimator.start()
        }

        // Global animator
        val animator: ValueAnimator = ValueAnimator.ofInt(0, 1)
        animator.addUpdateListener { callback.invoke() }
        animator.duration = duration
        animator.interpolator = interpolator
        animator.start()

        return this
    }
}
