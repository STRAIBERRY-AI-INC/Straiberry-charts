package com.straiberry.android.charts.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.straiberry.android.charts.AxisChartView
import com.straiberry.android.charts.ChartContract
import com.straiberry.android.charts.R
import com.straiberry.android.charts.animation.NoAnimation
import com.straiberry.android.charts.configuration.BarChartConfiguration
import com.straiberry.android.charts.configuration.ChartConfiguration
import com.straiberry.android.charts.data.*
import com.straiberry.android.charts.extenstions.drawChartBar
import com.straiberry.android.charts.extenstions.obtainStyledAttributes
import com.straiberry.android.charts.renderer.BarPercentChartRenderer
import com.straiberry.android.common.extensions.dp

class BarPercentChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AxisChartView(context, attrs, defStyleAttr), ChartContract.BarView {

    var spacing = DefaultSpacing

    @ColorInt
    var barsColor: Int = DefaultBarsColor

    @ColorInt
    var barsColorCurrentAverage: Int =
        ContextCompat.getColor(context, R.color.primary)

    @ColorInt
    var barsColorAverage: Int = ContextCompat.getColor(context, R.color.yellow200)

    @ColorInt
    var barsColorPreviousAverage: Int =
        ContextCompat.getColor(context, R.color.secondaryLight)

    var barsColorsList: List<Int>? = null

    var barRadius: Float = DefaultBarsRadius

    var currentAverage: Int = -1

    var average: Int = -1

    var previousAverage: Int = -1

    var barsBackgroundColor: Int = -1

    var isTooltipDraw: Boolean = false


    override val chartConfiguration: ChartConfiguration
        get() =
            BarChartConfiguration(
                width = measuredWidth,
                height = measuredHeight,
                paddings = Paddings(
                    left = paddingLeft.toFloat() + DefaultPaddingLeft,
                    top = paddingTop.toFloat() + PaddingTop,
                    right = paddingRight.toFloat() + DefaultPaddingRight,
                    bottom = paddingBottom.toFloat()
                ),
                axis = axis,
                labelsSize = labelsSize,
                scale = scale,
                barsBackgroundColor = barsBackgroundColor,
                barsSpacing = spacing,
                labelsFormatter = labelsFormatter
            )

    init {
        renderer = BarPercentChartRenderer(this, painter, NoAnimation())
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.BarChartAttrs))
        handleEditMode()
    }

    override fun drawBars(frames: List<Frame>) {
        // Show current,average and previous average
        frames.forEachIndexed { index, frame ->

            when (index) {
                currentAverage -> {
                    painter.prepare(color = barsColorCurrentAverage, style = Paint.Style.FILL)
                }
                average -> {
                    painter.prepare(color = barsColorAverage, style = Paint.Style.FILL)
                }
                previousAverage -> {
                    painter.prepare(color = barsColorPreviousAverage, style = Paint.Style.FILL)
                }
                else -> painter.prepare(color = barsColor, style = Paint.Style.FILL)
            }

            canvas.drawRoundRect(
                frame.toRectF(),
                barRadius,
                barRadius,
                painter.paint
            )
        }

        // Show tooltip
        if (currentAverage < 51)
            if (currentAverage != -1)
                if (!isTooltipDraw) {
                    tooltip.onDataPointClick(
                        frames[(currentAverage)].left, frames[(currentAverage)].y
                    )
                    isTooltipDraw = true
                }

        // Draw bottom line
        painter.prepare(
            color = barsColorCurrentAverage,
            style = Paint.Style.STROKE,
            strokeWidth = 5f
        )
        canvas.drawLine(
            frames.last().toRectF().right, frames.last().toRectF().bottom,
            frames.first().toRectF().right, frames.first().toRectF().bottom, painter.paint
        )
    }

    override fun drawBarsBackground(frames: List<Frame>) {
        painter.prepare(color = barsBackgroundColor, style = Paint.Style.FILL)
        frames.forEach {
            canvas.drawChartBar(
                it.toRectF(),
                barRadius,
                painter.paint
            )
        }

    }

    override fun drawLabelsX(xLabels: List<Label>) {
        painter.prepare(textSize = labelsSize, color = labelsColor, font = labelsFont)
        labels.draw(canvas, painter.paint, xLabels, labelsXColor)
    }

    override fun drawLabelsY(yLabels: List<Label>) {
        painter.prepare(textSize = labelsSize, color = labelsColor, font = labelsFont)
        labels.draw(canvas, painter.paint, yLabels, labelsYColor)
    }

    override fun drawGrid(
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    ) {
        grid.draw(canvas, innerFrame, xLabelsPositions, yLabelsPositions)
    }

    override fun drawDebugFrame(frames: List<Frame>) {
        painter.prepare(color = -0x1000000, style = Paint.Style.STROKE)
        frames.forEach { canvas.drawRect(it.toRect(), painter.paint) }

    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {
            spacing = getDimension(R.styleable.BarChartAttrs_chart_spacing, spacing)
            barsColor = getColor(R.styleable.BarChartAttrs_chart_barsColor, barsColor)
            barRadius = getDimension(R.styleable.BarChartAttrs_chart_barsRadius, barRadius)
            barsBackgroundColor =
                getColor(R.styleable.BarChartAttrs_chart_barsBackgroundColor, barsBackgroundColor)
            val resourceId = getResourceId(R.styleable.BarChartAttrs_chart_barsColorsList, -1)
            if (resourceId != -1)
                barsColorsList = resources.getIntArray(resourceId).toList()
            recycle()
        }
    }

    companion object {
        private const val DefaultSpacing = 10f
        private const val DefaultBarsColor = Color.BLACK
        private const val DefaultBarsRadius = 5F
        private val DefaultPaddingLeft = 25.dp
        private val PaddingTop = 35.dp
        private val DefaultPaddingRight = 25.dp
    }
}