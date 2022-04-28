package com.straiberry.android.charts.tooltip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.straiberry.android.charts.R
import com.straiberry.android.charts.Tooltip
import com.straiberry.android.common.extensions.dp
import com.straiberry.android.common.helper.ResizeAnimation

class PointTooltip : Tooltip {
    /**
     * Getting the average score
     */
    var average = 0

    private lateinit var tooltipView: View
    private lateinit var textViewAverage:TextView
    /**
     * Create point tooltip from layout
     * @see R.layout.percent_bar_chart_tooltip
     */
    override fun onCreateTooltip(parentView: ViewGroup) {
        tooltipView =
            LayoutInflater.from(parentView.context)
                .inflate(R.layout.percent_bar_chart_tooltip, parentView, false)
        tooltipView.apply {
            textViewAverage= findViewById(R.id.textViewAverage)
            textViewAverage.text = average.toString()
            parentView.addView(this)
        }
        isTooltipVisible = false
    }

    override fun onDataPointTouch(x: Float,  yTouch: Float,yPoint:Float) {}

    override fun onDataPointClick(x: Float, y: Float) {
        // Show tooltip just for first time and disable click
        if (!isTooltipVisible) {
            // Animate the tooltip
            tooltipView.animate().alpha(1F).duration = AlphaDuration
            val animation = ResizeAnimation(
                tooltipView, tooltipWith, 0, tooltipWith, 0, false
            )
            animation.duration = ResizeAnimationDuration
            tooltipView.startAnimation(animation)
            // Move tooltip to average score position
            tooltipView.x = x - tooltipWith / 2
            tooltipView.y = y - tooltipHeight
            isTooltipVisible = true
        }
    }

    override fun onActionUp() {
        textViewAverage.text = average.toString()
    }


    companion object {
        private var isTooltipVisible = false
        private val tooltipWith = 60.dp
        private val tooltipHeight = 60.dp
        private const val AlphaDuration = 500L
        private const val ResizeAnimationDuration = 1000L
    }
}
