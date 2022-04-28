package com.straiberry.android.charts

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.doOnPreDraw
import com.straiberry.android.charts.animation.ChartAnimation
import com.straiberry.android.charts.animation.DefaultAnimation
import com.straiberry.android.charts.configuration.ChartConfiguration
import com.straiberry.android.charts.data.AxisType
import com.straiberry.android.charts.data.DataPoint
import com.straiberry.android.charts.data.Frame
import com.straiberry.android.charts.data.Scale
import com.straiberry.android.charts.extenstions.obtainStyledAttributes
import com.straiberry.android.charts.extenstions.toDataPoints
import com.straiberry.android.charts.plugin.AxisGrid
import com.straiberry.android.charts.plugin.AxisLabels
import com.straiberry.android.charts.plugin.GridEffect
import com.straiberry.android.charts.plugin.GridType
import com.straiberry.android.charts.renderer.RendererConstants.Companion.NotInitialized

abstract class AxisChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var labelsSize: Float = DefaultLabelsSize

    var labelsColor: Int = -0x1000000

    var labelsXColor: Int = -0x1000000

    var labelsYColor: Int = -0x1000000

    var labelsFont: Typeface? = null

    var axis: AxisType = AxisType.XY

    var scale: Scale = Scale(NotInitialized, NotInitialized)

    var labelsFormatter: (Int) -> String = { it.toString() }

    var animation: ChartAnimation<DataPoint> = DefaultAnimation()

    val labels: Labels = AxisLabels()

    var tooltip: Tooltip = object : Tooltip {
        override fun onCreateTooltip(parentView: ViewGroup) {}
        override fun onDataPointTouch(x: Float, yTouch: Float, yPoint: Float) {}
        override fun onDataPointClick(x: Float, y: Float) {}
        override fun onActionUp() {}
    }

    var grid: Grid = object : Grid {
        override fun draw(
            canvas: Canvas,
            innerFrame: Frame,
            xLabelsPositions: List<Float>,
            yLabelsPositions: List<Float>
        ) {
        }
    }

    private var disableTouchAndClick = false

    var onDataPointClickListener: (index: Int, x: Float, y: Float) -> Unit = { _, _, _ -> }

    var onDataPointTouchListener: (index: Int, x: Float, y: Float) -> Unit = { _, _, _ -> }

    var onDataPointUnTouchListener: (index: Int, x: Float, y: Float) -> Unit = { _, _, _ -> }

    protected lateinit var canvas: Canvas

    protected val painter: Painter = Painter(labelsFont = labelsFont)

    /**
     * Initialized in init function by chart views extending [AxisChartView])
     */
    protected lateinit var renderer: ChartContract.Renderer

    lateinit var data: List<DataPoint>

    private val gestureDetector: GestureDetectorCompat =
        GestureDetectorCompat(
            this.context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent?): Boolean = true
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    val (index, x, y) = renderer.processClick(e?.x, e?.y)

                    return if (index != -1) {
                        onDataPointClickListener(index, x, y)
                        tooltip.onDataPointClick(x, y)
                        true
                    } else false
                }
            }
        )

    init {
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.ChartAttrs))
        doOnPreDraw {
            tooltip.onCreateTooltip(this)
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        setMeasuredDimension(
            if (widthMode == MeasureSpec.AT_MOST) DefaultFrameWidth else widthMeasureSpec,
            if (heightMode == MeasureSpec.AT_MOST) DefaultFrameHeight else heightMeasureSpec
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        renderer.draw()
    }

    fun disableTouchAndClick() {
        disableTouchAndClick = true
    }

    fun enableTouchAndClick() {
        disableTouchAndClick = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (!disableTouchAndClick) {
            val (index, x, y) = renderer.processTouch(event?.x, event?.y)
            if (index != -1) {
                onDataPointTouchListener(index, x, y)
                tooltip.onDataPointTouch(event?.x!!, event.y, y)
            }
            if (event?.actionMasked == MotionEvent.ACTION_UP) {
                onDataPointUnTouchListener(index, x, y)
                tooltip.onActionUp()
            }
            if (gestureDetector.onTouchEvent(event!!)) true
            else super.onTouchEvent(event)
        } else
            false
    }

    abstract val chartConfiguration: ChartConfiguration

    fun show(entries: List<Triple<Drawable?, String, Float>>) {
        doOnPreDraw { renderer.preDraw(chartConfiguration) }
        renderer.render(entries)
    }


    fun createBarPercent() {
        doOnPreDraw { renderer.preDraw(chartConfiguration) }
        val entries: ArrayList<Triple<Drawable?, String, Float>> = ArrayList()
        for (i in 10..60) {
            when (i) {
                10 -> entries.add(
                    Triple(null, "0", i.toFloat())
                )
                60 -> entries.add(
                    Triple(null, "100", i.toFloat())
                )
                else -> entries.add(
                    Triple(null, "", i.toFloat())
                )
            }
        }

        renderer.anim(entries, animation)
    }

    fun animate(entries: List<Triple<Drawable?, String, Float>>) {
        doOnPreDraw { renderer.preDraw(chartConfiguration) }
        renderer.anim(entries, animation)
        data = entries.toDataPoints()
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {

            // Customize Axis
            axis = when (getString(R.styleable.ChartAttrs_chart_axis)) {
                "0" -> AxisType.NONE
                "1" -> AxisType.X
                "2" -> AxisType.Y
                else -> AxisType.XY
            }

            // Customize Labels
            labelsSize = getDimension(R.styleable.ChartAttrs_chart_labelsSize, labelsSize)

            labelsColor = getColor(R.styleable.ChartAttrs_chart_labelsColor, labelsColor)
            labelsXColor = getColor(R.styleable.ChartAttrs_chart_labelsXColor, labelsXColor)
            labelsYColor = getColor(R.styleable.ChartAttrs_chart_labelsYColor, labelsYColor)

            if (hasValue(R.styleable.ChartAttrs_chart_labelsFont) && !isInEditMode) {
                labelsFont =
                    ResourcesCompat.getFont(
                        context,
                        getResourceId(R.styleable.ChartAttrs_chart_labelsFont, -1)
                    )
                painter.labelsFont = labelsFont
            }

            // Customize Grid
            if (hasValue(R.styleable.ChartAttrs_chart_grid)) {
                grid = AxisGrid().apply {
                    this.gridType = when (getString(R.styleable.ChartAttrs_chart_grid)) {
                        "0" -> GridType.FULL
                        "1" -> GridType.VERTICAL
                        "2" -> GridType.HORIZONTAL
                        else -> GridType.FULL
                    }
                    this.colorX = getColor(R.styleable.ChartAttrs_chart_gridColorX, color)
                    this.colorY = getColor(R.styleable.ChartAttrs_chart_gridColorY, color)
                    this.strokeWidth =
                        getDimension(R.styleable.ChartAttrs_chart_gridStrokeWidth, strokeWidth)
                    this.gridEffect =
                        when (getString(R.styleable.ChartAttrs_chart_gridEffect)) {
                            "0" -> GridEffect.SOLID
                            "1" -> GridEffect.DASHED
                            "2" -> GridEffect.DOTTED
                            else -> GridEffect.SOLID
                        }
                }
            }

            recycle()
        }
    }

    protected fun handleEditMode() {
        if (isInEditMode) {
            show(editModeSampleData)
        }
    }

    companion object {
        private const val DefaultFrameWidth = 200
        private const val DefaultFrameHeight = 100
        private const val DefaultLabelsSize = 25F
        private val editModeSampleData =
            listOf(
                Triple(null, "label 1", 5.5f),
                Triple(null, "label 3", 2.5f),
                Triple(null, "label 4", 2.7f)
            )
    }
}
