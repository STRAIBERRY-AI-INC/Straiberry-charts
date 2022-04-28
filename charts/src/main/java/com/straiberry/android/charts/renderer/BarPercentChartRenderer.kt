package com.straiberry.android.charts.renderer

import android.graphics.drawable.Drawable
import com.straiberry.android.charts.ChartContract
import com.straiberry.android.charts.Painter
import com.straiberry.android.charts.animation.ChartAnimation
import com.straiberry.android.charts.configuration.BarChartConfiguration
import com.straiberry.android.charts.configuration.ChartConfiguration
import com.straiberry.android.charts.configuration.toOuterFrame
import com.straiberry.android.charts.data.*
import com.straiberry.android.charts.extenstions.limits
import com.straiberry.android.charts.extenstions.maxValueBy
import com.straiberry.android.charts.extenstions.toDataPoints
import com.straiberry.android.charts.extenstions.toLabels
import com.straiberry.android.charts.renderer.executer.*
import kotlin.math.max

class BarPercentChartRenderer(
    private val view: ChartContract.BarView,
    private val painter: Painter,
    private var animation: ChartAnimation<DataPoint>
) : ChartContract.Renderer {

    private var data = emptyList<DataPoint>()

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private lateinit var chartConfiguration: BarChartConfiguration

    private lateinit var xLabels: List<Label>

    private lateinit var yLabels: List<Label>

    override fun preDraw(configuration: ChartConfiguration): Boolean {

        if (data.isEmpty()) return true

        chartConfiguration = configuration as BarChartConfiguration

        if (chartConfiguration.scale.notInitialized())
            chartConfiguration =
                chartConfiguration.copy(
                    scale = Scale(
                        min = 0F,
                        max = data.limits().second+ DefaultScale
                    )
                )

        xLabels = data.toLabels()
        val scaleStep = chartConfiguration.scale.size / RendererConstants.DefaultScaleNumberOfStepsForPercentChart
        yLabels = List(RendererConstants.DefaultScaleNumberOfStepsForPercentChart + 1) {
            val scaleValue = chartConfiguration.scale.min + scaleStep * it
            Label(
                label = chartConfiguration.labelsFormatter(scaleValue.toInt()),
                screenPositionX = 0F,
                screenPositionY = 0F,
                drawableScreenPositionX = 0F,
                drawableScreenPositionY = 0F
            )
        }

        val longestChartLabelWidth =
            yLabels.maxValueBy {
                painter.measureLabelWidth(
                    0,it.label,
                    chartConfiguration.labelsSize
                )
            }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureBarChartPaddings()(
            axisType = chartConfiguration.axis,
            labelsHeight = painter.measureLabelHeight(chartConfiguration.labelsSize),
            longestLabelWidth = longestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.LabelsPaddingToInnerChart
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

        placeLabelsX(innerFrame)
        placeLabelsY(innerFrame)
        placeDataPoints(innerFrame)

        animation.animateFrom(innerFrame.bottom, data) { view.postInvalidate() }

        return false
    }

    override fun draw() {

        if (data.isEmpty()) return

        if (chartConfiguration.axis.shouldDisplayAxisX())
            view.drawLabelsX(xLabels)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            view.drawLabelsY(yLabels)

        view.drawGrid(
            innerFrame,
            xLabels.map { it.screenPositionX },
            yLabels.map { it.screenPositionY }
        )

        if (chartConfiguration.barsBackgroundColor != -1)
            view.drawBarsBackground(
                GetVerticalBarBackgroundFrames()(
                    innerFrame,
                    chartConfiguration.barsSpacing,
                    data
                )
            )

        view.drawBars(
            GetVerticalBarFrames()(
                innerFrame,
                chartConfiguration.barsSpacing,
                data
            )
        )

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
                        DefineVerticalBarsClickableFrames()(
                            innerFrame,
                            data.map { Pair(it.screenPositionX, it.screenPositionY) }
                        )
            )
        }
    }

    override fun render(entries: List<Triple<Drawable?,String, Float>>) {
        data = entries.toDataPoints()
        view.postInvalidate()
    }

    override fun anim(entries: List<Triple<Drawable?,String, Float>>, animation: ChartAnimation<DataPoint>) {
        data = entries.toDataPoints()
        this.animation = animation
        view.postInvalidate()
    }

    override fun processClick(x: Float?, y: Float?): Triple<Int, Float, Float> {

        if (x == null || y == null || data.isEmpty())
            return Triple(-1, -1f, -1f)

        val index =
            DefineVerticalBarsClickableFrames()(
                innerFrame,
                data.map {
                    Pair(it.screenPositionX, it.screenPositionY)
                }
            )
                .indexOfFirst { it.contains(x, y) }

        return if (index != -1)
            Triple(index, data[index].screenPositionX, data[index].screenPositionY)
        else Triple(-1, -1f, -1f)
    }

    override fun processTouch(x: Float?, y: Float?): Triple<Int, Float, Float> = processClick(x, y)

    private fun placeLabelsX(innerFrame: Frame) {

        val halfBarWidth = (innerFrame.right - innerFrame.left) / xLabels.size / 2
        val labelsLeftPosition = innerFrame.left + halfBarWidth
        val labelsRightPosition = innerFrame.right - halfBarWidth
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
            (innerFrame.bottom - innerFrame.top) / RendererConstants.DefaultScaleNumberOfSteps
        val labelsBottomPosition =
            innerFrame.bottom + painter.measureLabelHeight(chartConfiguration.labelsSize) / 2

        yLabels.forEachIndexed { index, label ->
            label.screenPositionX =
                innerFrame.left -
                        RendererConstants.LabelsPaddingToInnerChart -
                        painter.measureLabelWidth(0,label.label, chartConfiguration.labelsSize) / 2
            label.screenPositionY = labelsBottomPosition - heightBetweenLabels * index
        }
    }

    private fun placeDataPoints(innerFrame: Frame) {

        val scaleSize = chartConfiguration.scale.size
        val chartHeight = innerFrame.bottom - innerFrame.top
        val halfBarWidth = (innerFrame.right - innerFrame.left) / xLabels.size / 2
        val labelsLeftPosition = innerFrame.left + halfBarWidth
        val labelsRightPosition = innerFrame.right - halfBarWidth
        val widthBetweenLabels = (labelsRightPosition - labelsLeftPosition) / (xLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX = labelsLeftPosition + (widthBetweenLabels * index)
            dataPoint.screenPositionY =
                innerFrame.bottom -
                        // bar length must be positive, or zero
                        (chartHeight * max(
                            0f,
                            dataPoint.value - chartConfiguration.scale.min
                        ) / scaleSize)
            dataPoint.drawableScreenPositionY=dataPoint.screenPositionY

        }
    }
    companion object{
        const val DefaultScale=20
    }
}
