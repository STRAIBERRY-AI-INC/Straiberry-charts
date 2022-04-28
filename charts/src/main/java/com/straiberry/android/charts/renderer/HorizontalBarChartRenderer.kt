package com.straiberry.android.charts.renderer

import android.graphics.drawable.Drawable
import com.straiberry.android.charts.ChartContract
import com.straiberry.android.charts.Painter
import com.straiberry.android.charts.animation.ChartAnimation
import com.straiberry.android.charts.configuration.BarChartConfiguration
import com.straiberry.android.charts.configuration.ChartConfiguration
import com.straiberry.android.charts.configuration.toOuterFrame
import com.straiberry.android.charts.data.*
import com.straiberry.android.charts.extenstions.maxValueBy
import com.straiberry.android.charts.extenstions.toDataPoints
import com.straiberry.android.charts.extenstions.toLabels
import com.straiberry.android.charts.renderer.executer.*
import com.straiberry.android.common.extensions.dp
import kotlin.math.max

enum class TypeOfXLabels { FromZeroToSeven, FromZeroToHundred, IsCharacter }
class HorizontalBarChartRenderer(
    private val view: ChartContract.BarView,
    private val painter: Painter,
    private var animation: ChartAnimation<DataPoint>,
    private val typeOfXLabels: TypeOfXLabels
) : ChartContract.Renderer {

    private var data = emptyList<DataPoint>()

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private lateinit var chartConfiguration: BarChartConfiguration

    private lateinit var xLabels: List<Label>

    private lateinit var yLabels: List<Label>

    private var typeOfDefaultNumberScaleSteps = RendererConstants.DefaultScaleNumberOfSteps

    private val min=0f

    private var max=0f

    private var paddingGridX=0

    override fun preDraw(configuration: ChartConfiguration): Boolean {

        if (data.isEmpty()) return true

        chartConfiguration = configuration as BarChartConfiguration

        /**
         * Change x label based on type. We have three type of horizontal chart:
         * @see TypeOfXLabels.IsCharacter : Label must be in range of "D-/A+"
         * @see TypeOfXLabels.FromZeroToHundred : Label must be in range of "0/100"
         * @see TypeOfXLabels.FromZeroToSeven : Label must ne in range of "0/7"
         */
        when (typeOfXLabels) {
            TypeOfXLabels.FromZeroToSeven -> {
                xLabels = List(xLabelsStringForZeroToSeven.size) {
                    Label(
                        label = xLabelsStringForZeroToSeven[it],
                        screenPositionX = 0F,
                        screenPositionY = 0F,
                        drawableScreenPositionY = 0f,
                        drawableScreenPositionX = 0f
                    )
                }
                max = RendererConstants.DefaultScaleNumberOfSteps.toFloat()
                typeOfDefaultNumberScaleSteps = RendererConstants.DefaultScaleNumberOfSteps
            }
            TypeOfXLabels.FromZeroToHundred -> {
                xLabels = List(xLabelsStringForZeroToHundred.size) {
                    Label(
                        label = xLabelsStringForZeroToHundred[it],
                        screenPositionX = 0F,
                        screenPositionY = 0F,
                        drawableScreenPositionY = 0f,
                        drawableScreenPositionX = 0f
                    )
                }
                max =
                    RendererConstants.DefaultScaleNumberOfStepsForHorizontalChartZeroToHundred.toFloat()
                typeOfDefaultNumberScaleSteps =
                    RendererConstants.DefaultScaleNumberOfStepsForHorizontalChart
            }

            TypeOfXLabels.IsCharacter -> {
                xLabels = List(xLabelsStringForCharacter.size) {
                    Label(
                        label = xLabelsStringForCharacter[it],
                        screenPositionX = 0F,
                        screenPositionY = 0F,
                        drawableScreenPositionY = 0f,
                        drawableScreenPositionX = 0f
                    )
                }
                max =
                    RendererConstants.DefaultScaleNumberOfStepsForHorizontalChartCharacterScale.toFloat()
                typeOfDefaultNumberScaleSteps =
                    RendererConstants.DefaultScaleNumberOfStepsForHorizontalChartCharacter
                paddingGridX= PaddingGridX
            }
        }

        /** Manage scale on every bar */
        if (chartConfiguration.scale.notInitialized())
            chartConfiguration =
                chartConfiguration.copy(
                    scale = Scale(
                        min = min,
                        max = max
                    )
                )

        yLabels = data.toLabels()

        val yLongestChartLabelWidth =
            yLabels.maxValueBy {
                painter.measureLabelWidth(
                    0, it.label,
                    chartConfiguration.labelsSize
                )
            }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureHorizontalBarChartPaddings()(
            axisType = chartConfiguration.axis,
            labelsHeight = painter.measureLabelHeight(chartConfiguration.labelsSize),
            xLastLabelWidth = painter.measureLabelWidth(
                0, xLabels.last().label,
                chartConfiguration.labelsSize
            ),
            yLongestLabelWidth = yLongestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.LabelsPaddingToInnerChart
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

        placeDataPoints(innerFrame)

        if (chartConfiguration.axis.shouldDisplayAxisX())
            placeLabelsX(innerFrame)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            placeLabelsY(outerFrame, innerFrame)



        animation.animateFrom(innerFrame.bottom, data) { view.postInvalidate() }

        return false
    }

    override fun draw() {

        if (data.isEmpty()) return

        if (chartConfiguration.barsBackgroundColor != -1)
            view.drawBarsBackground(
                GetHorizontalBarBackgroundFrames()(
                    innerFrame,
                    chartConfiguration.barsSpacing,
                    data
                )
            )

        view.drawBars(
            GetHorizontalBarFrames()(
                innerFrame,
                chartConfiguration.barsSpacing,
                data
            )
        )

        view.drawGrid(
            innerFrame,
            xLabels.map { it.screenPositionX + paddingGridX},
            yLabels.map { it.screenPositionY + PaddingGridY }
        )

        if (chartConfiguration.axis.shouldDisplayAxisX())
            view.drawLabelsX(xLabels)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            view.drawLabelsY(yLabels)

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
                        DefineHorizontalBarsClickableFrames()(
                            innerFrame,
                            data.map { Pair(it.screenPositionX, it.screenPositionY) }
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
            DefineHorizontalBarsClickableFrames()(
                innerFrame,
                data.map { Pair(it.screenPositionX, it.screenPositionY) }
            ).indexOfFirst { it.contains(x, y) }

        return if (index != -1)
            Triple(index, data[index].screenPositionX, data[index].screenPositionY)
        else Triple(-1, -1f, -1f)
    }

    override fun processTouch(x: Float?, y: Float?): Triple<Int, Float, Float> = processClick(x, y)

    private fun placeLabelsX(innerFrame: Frame) {

        val widthBetweenLabels =
            (innerFrame.right - innerFrame.left) / typeOfDefaultNumberScaleSteps
        val xLabelsVerticalPosition =
            innerFrame.bottom -
                    painter.measureLabelAscent(chartConfiguration.labelsSize) +
                    RendererConstants.LabelsPaddingToInnerChart

        xLabels.forEachIndexed { index, label ->
            label.screenPositionX = innerFrame.left + paddingX + widthBetweenLabels * index
            label.screenPositionY = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(outerFrame: Frame, innerFrame: Frame) {

        val halfBarWidth = (innerFrame.bottom - innerFrame.top) / yLabels.size / 2
        val labelsTopPosition = innerFrame.top + halfBarWidth
        val labelsBottomPosition = innerFrame.bottom - halfBarWidth
        val heightBetweenLabels = (labelsBottomPosition - labelsTopPosition) / (yLabels.size - 1)

        yLabels.forEachIndexed { index, label ->
            label.screenPositionX =
                innerFrame.left + paddingLabelX
            label.screenPositionY =
                labelsBottomPosition -
                        heightBetweenLabels * index +
                        painter.measureLabelDescent(chartConfiguration.labelsSize * 6)
            label.drawableScreenPositionX = innerFrame.left
            label.drawableScreenPositionY = labelsBottomPosition -
                    heightBetweenLabels * index +
                    painter.measureLabelDescent(chartConfiguration.labelsSize)
            label.screenPositionXDataPoint= data[index].screenPositionX
        }
    }

    private fun placeDataPoints(innerFrame: Frame) {

        val scaleSize = chartConfiguration.scale.max - chartConfiguration.scale.min
        val chartWidth = innerFrame.right - innerFrame.left
        val halfBarWidth = (innerFrame.bottom - innerFrame.top) / yLabels.size / 2
        val labelsBottomPosition = innerFrame.bottom - halfBarWidth
        val labelsTopPosition = innerFrame.top + halfBarWidth
        val heightBetweenLabels = (labelsBottomPosition - labelsTopPosition) / (yLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            if (dataPoint.value!=-1f) {
                dataPoint.screenPositionX =
                    innerFrame.left +
                            // bar length must be positive, or zero
                            (chartWidth * max(
                                0f,
                                dataPoint.value - chartConfiguration.scale.min
                            ) / scaleSize) + paddingX
                dataPoint.screenPositionY =
                    labelsBottomPosition -
                            heightBetweenLabels * index
            }
        }
    }

    companion object{
        private val paddingX= 65.dp
        private val paddingLabelX= 10.dp
        private val PaddingGridY=10.dp
        private val PaddingGridX=3.dp
        private val xLabelsStringForZeroToSeven =
            listOf("0", "1", "2", "3", "4", "5", "6", "7")

        private val xLabelsStringForZeroToHundred =
            listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100")

        private val xLabelsStringForCharacter =
            listOf("D-", "D", "D+", "C-", "C", "C+", "B-", "B", "B+", "A-", "A", "A+")
    }
}
