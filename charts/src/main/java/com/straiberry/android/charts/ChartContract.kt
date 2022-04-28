package com.straiberry.android.charts

import android.graphics.drawable.Drawable
import com.straiberry.android.charts.animation.ChartAnimation
import com.straiberry.android.charts.configuration.ChartConfiguration
import com.straiberry.android.charts.data.DataPoint
import com.straiberry.android.charts.data.Frame
import com.straiberry.android.charts.data.Label

interface ChartContract {

    interface AxisView {
        /**
         * Invalidate the axis to redraw again
         */
        fun postInvalidate()

        /**
         * Drawing all labels for x
         * @param xLabels
         * @see Label
         */
        fun drawLabelsX(xLabels: List<Label>)

        /**
         * Drawing all labels for y
         * @param yLabels
         * @see Label
         */
        fun drawLabelsY(yLabels: List<Label>)

        /**
         * Drawing grids on chart
         * @param innerFrame       for getting the inner frame position
         * @param xLabelsPositions for getting the x position of last label
         * @param yLabelsPositions for getting the y position of last label
         * Please refer to frame class for more details:
         * @see Frame
         */
        fun drawGrid(
            innerFrame: Frame,
            xLabelsPositions: List<Float>,
            yLabelsPositions: List<Float>
        )

        /**
         * Drawing the frame in debug mode
         */
        fun drawDebugFrame(frames: List<Frame>)
    }

    interface LineView : AxisView {
        /**
         * Draw line for linear chart view
         * @param points for getting data points
         */
        fun drawLine(points: List<DataPoint>)

        /**
         * Draw background on line chart. Using this we can draw
         * a gradient background from the start of line to bottom of chart.
         * @param innerFrame for getting inner frame position
         * @param points     for getting the list of data point
         * Please refer to data point class:
         * @see DataPoint
         */
        fun drawLineBackground(innerFrame: Frame, points: List<DataPoint>)

        /**
         * Draw a point on every data point in chart. Any data point that has a
         * value must draw a point if we needed.
         * @param points for getting the list of data point
         * Please refer to DataPoint class:
         * @see DataPoint
         */
        fun drawPoints(points: List<DataPoint>)
    }

    interface BarView : AxisView {
        /**
         * Drawing bars on bar chart view
         * @param frames for getting the list of bar with their position
         * For more detail please refer to Frame class:
         * @see Frame
         */
        fun drawBars(frames: List<Frame>)

        /**
         * Drawing a background on bars.
         * @param frames for getting the list of bar with their position
         * For more detail please refer to Frame class:
         * @see Frame
         */
        fun drawBarsBackground(frames: List<Frame>)
    }


    interface Renderer {
        fun preDraw(configuration: ChartConfiguration): Boolean
        fun draw()

        /**
         * If we don't want to animate the drawing process we use this method to
         * render and draw the entries on chart.
         * @param entries to get drawable, label text and score
         */
        fun render(entries: List<Triple<Drawable?, String, Float>>)

        /**
         * Using this method we animate the drawing operation.
         * @param entries to get data entries such as drawable , label text and score
         * @param animation to get the type of animation
         * @see ChartAnimation
         */
        fun anim(
            entries: List<Triple<Drawable?, String, Float>>,
            animation: ChartAnimation<DataPoint>
        )

        /**
         * This method will be used for getting the near data point where user click's.
         * @return Int for index of data point
         * @return Float for the x position of the data point
         * @return Float for the y position of the date point
         * @param x for x position of click event
         * @param y for y position of click event
         */
        fun processClick(x: Float?, y: Float?): Triple<Int, Float, Float>

        /**
         * This method will be used for getting the near data point where user touch's the frame chart.
         * @return Int for index of data point
         * @return Float for the x position of the data point
         * @return Float for the y position of the date point
         * @param x for x position of touch event
         * @param y for y position of touch event
         */
        fun processTouch(x: Float?, y: Float?): Triple<Int, Float, Float>
    }

}