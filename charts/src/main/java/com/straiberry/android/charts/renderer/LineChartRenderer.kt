package com.straiberry.android.charts.renderer

import android.graphics.drawable.Drawable
import com.straiberry.android.charts.ChartContract
import com.straiberry.android.charts.Painter
import com.straiberry.android.charts.animation.ChartAnimation
import com.straiberry.android.charts.configuration.ChartConfiguration
import com.straiberry.android.charts.configuration.LineChartConfiguration
import com.straiberry.android.charts.configuration.toOuterFrame
import com.straiberry.android.charts.data.*
import com.straiberry.android.charts.extenstions.maxValueBy
import com.straiberry.android.charts.extenstions.toDataPoints
import com.straiberry.android.charts.extenstions.toXLabelsLinearChart
import com.straiberry.android.charts.renderer.executer.*
import com.straiberry.android.common.extensions.dp


class LineChartRenderer(
    private val view: ChartContract.LineView,
    private val painter: Painter,
    private var animation: ChartAnimation<DataPoint>
) : ChartContract.Renderer {

    private var data = emptyList<DataPoint>()

    private val pointsWithoutEmptyScore=ArrayList<DataPoint>()

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private lateinit var chartConfiguration: LineChartConfiguration

    private lateinit var xLabels: List<Label>

    private lateinit var yLabels: List<Label>

    lateinit var frame: List<Frame>

    private val yLabelsString =
        listOf("D-", "D", "D+", "C-", "C", "C+", "B-", "B", "B+", "A-", "A", "A+")

    init {

    }
    override fun preDraw(configuration: ChartConfiguration): Boolean {

        if (data.isEmpty()) return true


        this.chartConfiguration = configuration as LineChartConfiguration

        if (chartConfiguration.scale.notInitialized())
            chartConfiguration = chartConfiguration.copy(
                scale = Scale(
                    min = 0F,
                    max = MaxScale
                )
            )

        xLabels = data.toXLabelsLinearChart()

        yLabels = List(yLabelsString.size) {
            Label(
                label = yLabelsString[it],
                screenPositionX = 0F,
                screenPositionY = 0F,
                drawableScreenPositionY = 0f,
                drawableScreenPositionX = 0f
            )
        }


        val longestChartLabelWidth =
            yLabels.maxValueBy {
                painter.measureLabelWidth(
                    text = it.label,
                    textSize = chartConfiguration.labelsSize,
                    yLabelPadding = 0
                )
            }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureLineChartPaddings()(
            axisType = chartConfiguration.axis,
            labelsHeight = painter.measureLabelHeight(chartConfiguration.labelsSize),
            longestLabelWidth = longestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.LabelsPaddingToInnerChart,
            lineThickness = chartConfiguration.lineThickness,
            pointsDrawableWidth = chartConfiguration.pointsDrawableWidth,
            pointsDrawableHeight = chartConfiguration.pointsDrawableHeight
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

        frame = GetVerticalBarFrames()(
            innerFrame,
            0f,
            data
        )

        placeLabelsX(innerFrame)
        placeLabelsY(innerFrame)
        placeDataPoints(innerFrame)

        pointsWithoutEmptyScore.clear()
        // Remove points that has no data
        data.forEachIndexed { _, dataPoint ->
            if (dataPoint.value!=0f)
                pointsWithoutEmptyScore.add(dataPoint)
        }
        animation.animateFrom(innerFrame.bottom, data) { view.postInvalidate() }

        return false
    }

    override fun draw() {

        if (data.isEmpty()) return


        if (chartConfiguration.axis.shouldDisplayAxisX())
            view.drawLabelsX(xLabels)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            view.drawLabelsY(yLabels)



        if (chartConfiguration.fillColor != 0 ||
            chartConfiguration.gradientFillColors.isNotEmpty()
        )
            view.drawLineBackground(innerFrame, pointsWithoutEmptyScore)
        view.drawLine(pointsWithoutEmptyScore)
        view.drawGrid(
            innerFrame,
            xLabels.filter { it.label!=""}.map { it.screenPositionX },
            yLabels.filter { it.label!=""}.map { it.screenPositionY }
        )
        if (chartConfiguration.pointsDrawableWidth != -1)
            view.drawPoints(data)

        if (RendererConstants.InDebug) {
            view.drawDebugFrame(
                listOf(outerFrame, innerFrame) +
                        DebugWithLabelsFrame()(
                            painter = painter,
                            axisType = chartConfiguration.axis,
                            xLabels = xLabels,
                            yLabels = yLabels,
                            labelsSize = chartConfiguration.labelsSize
                        ) +
                        DefineDataPointsClickableFrames()(
                            innerFrame = innerFrame,
                            datapointsCoordinates = data.map {
                                Pair(
                                    it.screenPositionX,
                                    it.screenPositionY
                                )
                            },
                            clickableRadius = chartConfiguration.clickableRadius
                        )
            )
        }
    }

    override fun render(entries: List<Triple<Drawable?, String, Float>>) {
        data = entries.toDataPoints()
        view.postInvalidate()
    }

    override fun anim(
        entries: List<Triple<Drawable?, String, Float>>,
        animation: ChartAnimation<DataPoint>
    ) {
        data = entries.toDataPoints()
        this.animation = animation
        view.postInvalidate()
    }

    override fun processClick(x: Float?, y: Float?): Triple<Int, Float, Float> {

        if (x == null || y == null || data.isEmpty())
            return Triple(-1, -1f, -1f)

        val index =
            DefineDataPointsClickableFrames()(
                innerFrame,
                data.map { Pair(it.screenPositionX, it.screenPositionY) },
                chartConfiguration.clickableRadius
            ).indexOfFirst { it.contains(x, y) }

        return if (index != -1)
            Triple(index, data[index].screenPositionX, data[index].screenPositionY)
        else Triple(-1, -1f, -1f)
    }

    override fun processTouch(x: Float?, y: Float?): Triple<Int, Float, Float> {
        if (x == null || y == null)
            return Triple(-1, -1f, -1f)

        val index =
            DefineVerticalTouchableFrames()(
                innerFrame,
                data.map {Pair(it.screenPositionX, it.screenPositionY) }
            ).indexOfFirst {
                it.contains(x, y) }

        return if (index != -1)
            Triple(index, data[index].screenPositionX, data[index].screenPositionY)
        else Triple(-1, -1f, -1f)
    }

    private fun placeLabelsX(innerFrame: Frame) {

        val labelsLeftPosition =
            innerFrame.left +
                    painter.measureLabelWidth(
                        0,
                        xLabels.first().label,
                        chartConfiguration.labelsSize
                    ) / 2
        val labelsRightPosition =
            innerFrame.right -
                    painter.measureLabelWidth(
                        0,
                        xLabels.last().label,
                        chartConfiguration.labelsSize
                    ) / 2
        val widthBetweenLabels = (labelsRightPosition - labelsLeftPosition) / (xLabels.size - 1)
        val xLabelsVerticalPosition =
            innerFrame.bottom -
                    painter.measureLabelAscent(chartConfiguration.labelsSize) +
                    RendererConstants.LabelsPaddingToInnerChart

        xLabels.forEachIndexed { index, label ->
            label.screenPositionX = labelsLeftPosition + (widthBetweenLabels * index)
            label.screenPositionY = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(innerFrame: Frame) {

        val heightBetweenLabels =
            (innerFrame.bottom - innerFrame.top) / RendererConstants.DefaultScaleNumberOfStepsForLineChart
        val labelsBottomPosition =
            innerFrame.bottom + painter.measureLabelHeight(chartConfiguration.labelsSize) / 5
        val xLabelPosition= innerFrame.left -
                RendererConstants.LabelsPaddingToInnerChart -
                painter.measureLabelWidth(
                    0,
                    yLabels[0].label,
                    chartConfiguration.labelsSize + LabelPadding
                )

        yLabels.forEachIndexed { index, label ->
            label.screenPositionX =
                xLabelPosition
            label.screenPositionY = labelsBottomPosition - heightBetweenLabels * index
        }
    }

    private fun placeDataPoints(innerFrame: Frame) {

        val scaleSize = chartConfiguration.scale.size
        val chartHeight = innerFrame.bottom - innerFrame.top
        val widthBetweenLabels = (innerFrame.right - innerFrame.left) / (xLabels.size -1)
        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX = innerFrame.left + (widthBetweenLabels * index)
            dataPoint.screenPositionY =
                innerFrame.bottom -
                        (chartHeight * (dataPoint.value - chartConfiguration.scale.min) / scaleSize)
            dataPoint.drawableScreenPositionY = dataPoint.screenPositionY
        }
    }

    companion object{
        private val LabelPadding = 4.dp
        private const val MaxScale=13f
    }
}
