package com.straiberry.android.charts.data

import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.graphics.toRectF

data class Frame(val left: Float=0f, val top: Float=0f, val right: Float=0f, val bottom: Float=0f,val y:Float=0f)

fun Frame.toRect(): Rect =
    Rect(this.left.toInt(), this.top.toInt(), this.right.toInt(), this.bottom.toInt())

fun Frame.toRectF(): RectF = toRect().toRectF()

fun Frame.withPaddings(paddings: Paddings): Frame =
    Frame(
        left = left + paddings.left,
        top = top + paddings.top,
        right = right - paddings.right,
        bottom = bottom - paddings.bottom
    )

fun Frame.toLinearGradient(gradientColors: IntArray): LinearGradient {
    return LinearGradient(
        left,
        top,
        left,
        bottom,
        gradientColors[0],
        gradientColors[1],
        Shader.TileMode.CLAMP
    )
}

fun Frame.contains(x: Float, y: Float): Boolean =
    left < right && top < bottom && // check for empty first
        x >= left && x < right && y >= top && y < bottom
