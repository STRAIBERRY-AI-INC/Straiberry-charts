package com.straiberry.android.charts.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.annotation.Size
import com.straiberry.android.charts.AxisChartView
import com.straiberry.android.charts.ChartContract
import com.straiberry.android.charts.R
import com.straiberry.android.charts.animation.NoAnimation
import com.straiberry.android.charts.configuration.ChartConfiguration
import com.straiberry.android.charts.configuration.LineChartConfiguration
import com.straiberry.android.charts.data.*
import com.straiberry.android.charts.extenstions.*
import com.straiberry.android.charts.renderer.LineChartRenderer
import com.straiberry.android.charts.tooltip.SliderTooltip
import com.straiberry.android.common.extensions.dp


class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AxisChartView(context, attrs, defStyleAttr),
    ChartContract.LineView {

    var smooth: Boolean = DefaultSmooth

    var lineThickness: Float = DefaultLineThickness

    var fillColor: Int = DefaultFillColor

    var lineColor: Int = DefaultLineColor

    @Size(2)
    var gradientFillColors: IntArray = intArrayOf(0, 0)

    @DrawableRes
    var pointsDrawableRes = -1

    var isTooltipDraw: Boolean = false

    var isFake: Boolean = false

    override val chartConfiguration: ChartConfiguration
        get() =
            LineChartConfiguration(
                width = measuredWidth,
                height = measuredHeight,
                paddings = Paddings(
                    paddingLeft.toFloat() + PaddingLeft,
                    paddingTop.toFloat() + PaddingTop,
                    paddingRight.toFloat() + PaddingRight,
                    paddingBottom.toFloat()
                ),
                axis = axis,
                labelsSize = labelsSize,
                lineThickness = lineThickness,
                scale = scale,
                pointsDrawableWidth = if (pointsDrawableRes != -1)
                    getDrawable(pointsDrawableRes)!!.intrinsicWidth else -1,
                pointsDrawableHeight = if (pointsDrawableRes != -1)
                    getDrawable(pointsDrawableRes)!!.intrinsicHeight else -1,
                fillColor = fillColor,
                gradientFillColors = gradientFillColors,
                labelsFormatter = labelsFormatter,
                clickableRadius = DefaultClickableArea.toPx()
            )

    init {
        renderer = LineChartRenderer(this, painter, NoAnimation())
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.LineChartAttrs))
        handleEditMode()
        tooltip.onCreateTooltip(this)

    }

    override fun drawLine(points: List<DataPoint>) {
        if (points.isNotEmpty()) {
            val linePath =
                if (!smooth) points.toLinePath()
                else points.toSmoothLinePath(DefaultSmoothFactor)

            painter.prepare(
                color = lineColor,
                style = Paint.Style.STROKE,
                strokeWidth = lineThickness
            ).apply {
                setShadowLayer(10f, 0f, 5f, Color.GRAY)
            }
            canvas.drawPath(linePath, painter.paint)
            SliderTooltip.path = linePath
        }
        drawTooltip(points)

    }

    private fun drawTooltip(points: List<DataPoint>) {
        // Show tool tip in last point position when chart is created
        if (!points.isNullOrEmpty())
            if (!isTooltipDraw) {
                if (isFake)
                    tooltip.onDataPointTouch(
                        points.first().screenPositionX,
                        0f,
                        points.first().drawableScreenPositionY
                    )
                else
                    tooltip.onDataPointTouch(
                        points.last().screenPositionX,
                        0f,
                        points.last().drawableScreenPositionY
                    )
                isTooltipDraw = true
            }
    }

    override fun drawLineBackground(innerFrame: Frame, points: List<DataPoint>) {
        if (points.isNotEmpty()) {
            val linePath =
                if (!smooth) points.toLinePath()
                else points.toSmoothLinePath(DefaultSmoothFactor)
            val backgroundPath = createBackgroundPath(linePath, points, innerFrame.bottom)
            if (fillColor != 0)
                painter.prepare(color = fillColor, style = Paint.Style.FILL)
            else
                painter.prepare(
                    shader = innerFrame.toLinearGradient(gradientFillColors),
                    style = Paint.Style.FILL
                )

            canvas.drawPath(backgroundPath, painter.paint)
        }

    }

    override fun drawGrid(
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    ) {
        grid.draw(canvas, innerFrame, xLabelsPositions, yLabelsPositions)
    }

    override fun drawPoints(points: List<DataPoint>) {
        if (pointsDrawableRes != -1) {
            points.forEach { dataPoint ->
                getDrawable(pointsDrawableRes)?.let {
                    it.centerAt(dataPoint.screenPositionX, dataPoint.screenPositionY)
                    it.draw(canvas)
                }
            }
        }
    }

    override fun drawLabelsX(xLabels: List<Label>) {
        painter.prepare(textSize = labelsSize, color = labelsColor, font = labelsFont)
            .setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
        labels.draw(canvas, painter.paint, xLabels, labelsXColor)
    }

    override fun drawLabelsY(yLabels: List<Label>) {
        painter.prepare(textSize = labelsSize, color = labelsColor, font = labelsFont)
            .setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
        labels.draw(canvas, painter.paint, yLabels, labelsYColor)
    }

    override fun drawDebugFrame(frames: List<Frame>) {
        painter.prepare(color = -0x1000000, style = Paint.Style.STROKE)
        frames.forEach { canvas.drawRect(it.toRect(), painter.paint) }
    }

    private fun createBackgroundPath(
        path: Path,
        points: List<DataPoint>,
        innerFrameBottom: Float
    ): Path {

        val res = Path(path)

        res.lineTo(points.last().screenPositionX, innerFrameBottom)
        res.lineTo(points.first().screenPositionX, innerFrameBottom)
        res.close()

        return res
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {
            lineColor = getColor(R.styleable.LineChartAttrs_chart_lineColor, lineColor)
            lineThickness =
                getDimension(R.styleable.LineChartAttrs_chart_lineThickness, lineThickness)
            smooth = getBoolean(R.styleable.LineChartAttrs_chart_smoothLine, smooth)
            pointsDrawableRes =
                getResourceId(R.styleable.LineChartAttrs_chart_pointsDrawable, pointsDrawableRes)
            recycle()
        }
    }

    companion object {
        private const val DefaultSmoothFactor = 0.25f
        private const val DefaultSmooth = true
        private val DefaultLineThickness = (3f).dp
        private const val DefaultFillColor = 0
        private const val DefaultLineColor = 0
        private const val DefaultClickableArea = 24 // dp
        private val PaddingLeft = 15.dp
        private val PaddingTop = 55.dp
        private val PaddingRight = 30.dp
    }
}
