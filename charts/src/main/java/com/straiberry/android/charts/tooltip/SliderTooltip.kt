package com.straiberry.android.charts.tooltip

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.straiberry.android.charts.R
import com.straiberry.android.charts.Tooltip
import com.straiberry.android.charts.data.DataPoint
import com.straiberry.android.charts.databinding.LineChartTooltipBinding
import com.straiberry.android.common.extensions.dp
import com.straiberry.android.common.extensions.gone
import com.straiberry.android.common.extensions.hide
import com.straiberry.android.common.extensions.visible


class SliderTooltip : Tooltip {

    /** Getting score average*/
    var scoreAverage = ""

    /** Getting the first score*/
    var scoreOne = ""

    /** Getting second score */
    var scoreTwo = ""

    var isDateHasMoreThenTwoScore = false

    var date = ""

    var isFake = false
    var isFirstTimeCreated = true

    private lateinit var parentViews: ViewGroup
    private lateinit var binding: LineChartTooltipBinding

    /**
     * Create tooltip from layout
     * @see R.layout.line_chart_tooltip
     */
    override fun onCreateTooltip(parentView: ViewGroup) {
        binding = LineChartTooltipBinding.inflate(
            LayoutInflater.from(parentView.context),
            parentView,
            false
        )
        parentViews = parentView
        parentView.addView(binding.root)
    }

    /**
     * Setting up the touch event
     */
    override fun onDataPointTouch(x: Float, yTouch: Float, yPoint: Float) {
        try {
            val point = FloatArray(2)
            val pathMeasure = PathMeasure(path, true)
            val pBounds = RectF()
            path?.computeBounds(pBounds, true)
            // Check if tool tip is created for first time then move it to last point
            if (isFirstTimeCreated) {
                binding.rootLayout.visible()
                // Change the position of tool tip to first point
                binding.rootLayout.apply {
                    this.x = x - tooltipWith / 2
                    this.y = yPoint - tooltipHeight
                }
                setupPreviousData(x, yPoint)
                setupData()
                hideShowLayoutDate()
                isFirstTimeCreated = false
            } else {
                // Check if touch point is in line path
                pathMeasure.getPosTan(x - pBounds.left, point, null)
                if (x in pBounds.left..pBounds.right) {
                    binding.rootLayout.apply {
                        this.x = x - binding.root.width / 2
                        this.y = point.last() - binding.root.height / 2
                    }
                    setupPreviousData(x, yPoint)
                    binding.layoutDate.hide()
                    binding.layoutDateRotate.hide()
                }
            }
        } catch (e: Exception) {
            Log.e("Tooltip not initialized", e.toString())
        }
    }

    override fun onDataPointClick(x: Float, y: Float) {}

    /**
     * When user trigger the up action the tooltip moves to
     * last point that does have a score
     */
    override fun onActionUp() {
        try {
            binding.rootLayout.animate()?.translationX(previousX - binding.root.width / 2)
            binding.rootLayout.animate()?.translationY(previousY - binding.root.height / 2)
            hideShowLayoutDate()
            binding.textViewScoreAverage.startAnimation(
                AnimationUtils.loadAnimation(
                    parentViews.context,
                    R.anim.anim_text_change
                )
            )
            setupData()
        } catch (e: Exception) {
            print("Tooltip has not initialized yet")
        }
    }

    /*** If score is A+ then rotate the date box */
    private fun hideShowLayoutDate() {
        if (previousScoreAverage == "A+") {
            binding.layoutDate.hide()
            binding.layoutDateRotate.visible()
        } else {
            binding.layoutDate.visible()
            binding.layoutDateRotate.hide()
        }
    }

    /**
     * setting up the date for first time that the tooltip is showing
     */
    private fun setupData() {
        binding.textViewScoreAverage.text = previousScoreAverage
        binding.textViewDateOne.text = previousDate
        binding.textViewDateOneRotate.text = previousDate
        showMore()
    }

    private fun showMore() {
        if (previousScoreOne != "" && previousScoreTwo != "") {
            binding.textViewMoreThenTwoScore.visible()
            binding.textViewMoreThenTwoScoreRotate.visible()
        } else {
            binding.textViewMoreThenTwoScoreRotate.gone()
            binding.textViewMoreThenTwoScore.gone()
        }
    }

    /**
     * If a date does have a score we save their variables as a
     * previous date. User can not select a date that does not have a score.
     */
    private fun setupPreviousData(x: Float, y: Float) {
        showMore()
        if (scoreAverage != "0" && isFake.not()) {
            previousX = x
            previousY = y
            previousScoreAverage = scoreAverage
            previousDate = date
            previousScoreOne = scoreOne
            previousScoreTwo = scoreTwo
            previousIsDateHasMoreThenTwoScore = isDateHasMoreThenTwoScore
        }
    }

    companion object {
        private val tooltipWith = 65.dp
        private var tooltipHeight = 70.dp
        private var previousX = 0f
        private var previousY = 0f
        private var previousScoreAverage = ""
        private var previousScoreOne = ""
        private var previousScoreTwo = ""
        private var previousIsDateHasMoreThenTwoScore = false
        private var previousDate = ""
        var path: Path? = null
        var dataPoint: List<DataPoint>? = null
    }
}
