package com.straiberry.android.common.custom.spotlight.shape

import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF

/**
 * Shape of a [Target] that would be drawn by Spotlight View.
 * For any shape of target, this Shape class need to be implemented.
 */
interface Shape {

  /**
   * [duration] to draw Shape.
   */
  val duration: Long

  /**
   * [interpolator] to draw Shape.
   */
  val interpolator: TimeInterpolator

  /**
   * Shape size
   */
  val radius : Float

  /**
   * Draws the Shape.
   *
   * @param value the animated value from 0 to 1.
   */
  fun draw(canvas: Canvas, point: PointF, value: Float, paint: Paint,context:Context)
}