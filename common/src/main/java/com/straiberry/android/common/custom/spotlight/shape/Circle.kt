package com.straiberry.android.common.custom.spotlight.shape

import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.straiberry.android.common.R
import java.util.concurrent.TimeUnit

/**
 * [Shape] of Circle with customizable radius.
 */
class Circle @JvmOverloads constructor(
    override val radius: Float,
    override val duration: Long = DEFAULT_DURATION,
    override val interpolator: TimeInterpolator = DEFAULT_INTERPOLATOR
) : Shape {

    override fun draw(canvas: Canvas, point: PointF, value: Float, paint: Paint, context: Context) {
        canvas.drawCircle(point.x, point.y, value * AnimationScaleSize + radius, paint)

        // First border
        val shapeFirstBorderPaint by lazy {
            Paint().apply {
                color = ContextCompat.getColor(context, R.color.primaryLight300Opacity62)
                strokeWidth = StrokeWidthFirstBorder
                style = Paint.Style.STROKE
            }
        }
        canvas.drawCircle(point.x, point.y, value * AnimationScaleSize + radius + FirstBorderSize , shapeFirstBorderPaint)

        // Second border
        val shapeSecondBorderPaint by lazy {
            Paint().apply {
                color = ContextCompat.getColor(context, R.color.headlinesLabel)
                strokeWidth = StrokeWidthSecondBorder
                style = Paint.Style.STROKE
            }
        }
        canvas.drawCircle(point.x, point.y, value * AnimationScaleSize + radius + SecondBorderSize , shapeSecondBorderPaint)

    }

  companion object {
        const val AnimationScaleSize = 40
        const val StrokeWidthFirstBorder = 30f
        const val StrokeWidthSecondBorder = 20f
        const val FirstBorderSize = 20f
        const val SecondBorderSize = 35f
        val DEFAULT_DURATION = TimeUnit.MILLISECONDS.toMillis(500)

        val DEFAULT_INTERPOLATOR = DecelerateInterpolator(2f)
    }
}
