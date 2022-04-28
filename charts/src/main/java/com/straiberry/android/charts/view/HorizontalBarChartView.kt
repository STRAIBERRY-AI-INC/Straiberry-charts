package com.straiberry.android.charts.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.straiberry.android.charts.AxisChartView
import com.straiberry.android.charts.ChartContract
import com.straiberry.android.charts.R
import com.straiberry.android.charts.animation.DefaultHorizontalAnimation
import com.straiberry.android.charts.animation.NoAnimation
import com.straiberry.android.charts.configuration.BarChartConfiguration
import com.straiberry.android.charts.configuration.ChartConfiguration
import com.straiberry.android.charts.data.*
import com.straiberry.android.charts.extenstions.drawChartBar
import com.straiberry.android.charts.extenstions.obtainStyledAttributes
import com.straiberry.android.charts.renderer.HorizontalBarChartRenderer
import com.straiberry.android.charts.renderer.TypeOfXLabels
import com.straiberry.android.common.extensions.dp

class HorizontalBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AxisChartView(context, attrs, defStyleAttr), ChartContract.BarView {

    var spacing = DefaultSpacing

    @ColorInt
    var barsColor: Int = DefaultBarsColor

    var barsColorsList: List<Int>? = null

    var barRadius: Float = DefaultBarsRadius

    var barsBackgroundColor: Int = -1

    var typeOfXLabels: TypeOfXLabels = TypeOfXLabels.FromZeroToSeven

    override val chartConfiguration: ChartConfiguration
        get() = BarChartConfiguration(
            measuredWidth,
            measuredHeight,
            Paddings(
                paddingLeft.toFloat() + DefaultPaddingLeft,
                paddingTop.toFloat() + DefaultPaddingTop,
                paddingRight.toFloat() + DefaultPaddingRight,
                paddingBottom.toFloat()
            ),
            axis,
            labelsSize - DefaultPaddingSize,
            scale,
            labelsFormatter,
            barsBackgroundColor,
            spacing
        )

    init {
        animation = DefaultHorizontalAnimation()
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.BarChartAttrs))
        renderer = HorizontalBarChartRenderer(this, painter, NoAnimation(), typeOfXLabels)
        handleEditMode()
    }

    override fun drawBars(frames: List<Frame>) {
        if (!frames.isNullOrEmpty()) {
            if (barsColorsList == null)
                barsColorsList = List(frames.size) { barsColor }.toList()

            if (barsColorsList!!.size != frames.size)
                throw IllegalArgumentException("Colors provided do not match the number of datapoints.")

            frames.forEachIndexed { index, frame ->
                painter.prepare(color = barsColorsList!![index], style = Paint.Style.FILL)
                canvas.drawChartBar(
                    frame.toRectF(),
                    barRadius,
                    painter.paint
                )
            }
        }
    }

    override fun drawBarsBackground(frames: List<Frame>) {
        painter.prepare(color = barsBackgroundColor, style = Paint.Style.FILL)
        frames.forEach {
            canvas.drawRoundRect(
                it.toRectF(),
                barRadius,
                barRadius,
                painter.paint
            )
        }
    }

    override fun drawLabelsX(xLabels: List<Label>) {
        painter.prepare(textSize = labelsSize, color = labelsColor, font = labelsFont)
        labels.draw(canvas, painter.paint, xLabels,labelsXColor)
    }

    override fun drawLabelsY(yLabels: List<Label>) {
        painter.prepare(textSize = labelsSize, color = labelsColor, font = labelsFont)
        labels.draw(canvas, painter.paint, yLabels,labelsYColor)
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
            typeOfXLabels = when (getString(R.styleable.BarChartAttrs_chart_horizontal_y_label_type)) {
                "0" -> TypeOfXLabels.FromZeroToSeven
                "1" -> TypeOfXLabels.FromZeroToHundred
                "2" -> TypeOfXLabels.IsCharacter
                else -> TypeOfXLabels.FromZeroToSeven
            }
            recycle()
        }
    }

    companion object {
        private const val DefaultPaddingSize = 20
        private val DefaultPaddingRight= 35.dp
        private val DefaultPaddingLeft= 10.dp
        private val DefaultPaddingTop= 5.dp
        private const val DefaultSpacing = 10f
        private const val DefaultBarsColor = -0x1000000
        private const val DefaultBarsRadius = 0F
    }
}
