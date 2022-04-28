package com.straiberry.android.charts.extenstions

import android.graphics.Path
import android.graphics.drawable.Drawable
import com.straiberry.android.charts.data.DataPoint
import com.straiberry.android.charts.data.Label
import com.straiberry.android.charts.data.Scale
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun List<DataPoint>.limits(): Pair<Float, Float> {

    if (isEmpty())
        Pair(0F, 1F)

    val values = map { it.value }
    return values.floatLimits()
}

fun List<DataPoint>.toScale(): Scale {
    val limits = limits()
    return Scale(min = limits.first, max = limits.second)
}

fun List<DataPoint>.toLabels(): List<Label> {
    return map {
        Label(
            drawable = it.drawable,
            label = it.label,
            screenPositionX = 0f,
            screenPositionY = 0f,
            drawableScreenPositionX = 0f,
            drawableScreenPositionY = 0f
        )
    }
}

private const val OneWeak=7L
private const val TwoWeak=14L
private const val ThreeWeak=21L
private const val FourthWeak=28L

fun List<DataPoint>.toXLabelsLinearChart(): List<Label> {
    val currentTime= System.currentTimeMillis()
    val oneWeak= (currentTime - TimeUnit.DAYS.toMillis(OneWeak)).getDate()
    val twoWeak= (currentTime - TimeUnit.DAYS.toMillis(TwoWeak)).getDate()
    val threeWeak= (currentTime - TimeUnit.DAYS.toMillis(ThreeWeak)).getDate()
    val fourthWeak= (currentTime - TimeUnit.DAYS.toMillis(FourthWeak)).getDate()
    return mapIndexed { index, dataPoint ->
        Label(
            drawable = dataPoint.drawable,
            label = when (index) {
                this.size - OneWeak.toInt() -> oneWeak!!
                this.size - TwoWeak.toInt() -> twoWeak!!
                this.size - ThreeWeak.toInt() -> threeWeak!!
                this.size - FourthWeak.toInt() -> fourthWeak!!
                else ->""
            },
            screenPositionX = 0f,
            screenPositionY = 0f,
            drawableScreenPositionX = 0f,
            drawableScreenPositionY = 0f
        )
    }
}

private fun Long.getDate(): String? {
    val date = Date(this) // *1000 is to convert seconds to milliseconds
    val sdf = SimpleDateFormat("dd MMM") // the format of your date
    sdf.timeZone = TimeZone.getTimeZone("GMT-4")
    return sdf.format(date)
}

fun List<DataPoint>.toLinePath(): Path {
    val res = Path()

    res.moveTo(this.first().screenPositionX, this.first().screenPositionY)
    for (i in 1 until this.size)
        res.lineTo(this[i].screenPositionX, this[i].screenPositionY)
    return res
}


fun List<DataPoint>.toSmoothLinePath(smoothFactor: Float): Path {

    var thisPointX: Float
    var thisPointY: Float
    var nextPointX: Float
    var nextPointY: Float
    var startDiffX: Float
    var startDiffY: Float
    var endDiffX: Float
    var endDiffY: Float
    var firstControlX: Float
    var firstControlY: Float
    var secondControlX: Float
    var secondControlY: Float

    val res = Path()
    res.moveTo(this.first().screenPositionX, this.first().screenPositionY)
    for (i in 0 until this.size - 1) {

        nextPointX = this[i + 1].screenPositionX
        nextPointY = this[i + 1].screenPositionY

        thisPointX = this[i].screenPositionX
        thisPointY = this[i].screenPositionY

        startDiffX = nextPointX - this[si(this.size, i - 1)].screenPositionX
        startDiffY = nextPointY - this[si(this.size, i - 1)].screenPositionY

        endDiffX = this[si(this.size, i + 2)].screenPositionX - thisPointX
        endDiffY = this[si(this.size, i + 2)].screenPositionY - thisPointY

        firstControlX = thisPointX + smoothFactor * startDiffX
        firstControlY = thisPointY + smoothFactor * startDiffY

        secondControlX = nextPointX - smoothFactor * endDiffX
        secondControlY = nextPointY - smoothFactor * endDiffY
        res.cubicTo(
            firstControlX,
            firstControlY,
            secondControlX,
            secondControlY,
            nextPointX,
            nextPointY
        )

    }

    return res
}

private fun List<Float>.floatLimits(): Pair<Float, Float> {

    val min = minOrNull() ?: 0F
    var max = maxOrNull() ?: 1F

    if (min == max)
        max += 1F

    return Pair(min, max)
}


private fun si(setSize: Int, i: Int): Int {
    return when {
        i > setSize - 1 -> setSize - 1
        i < 0 -> 0
        else -> i
    }
}

internal fun List<Triple<Drawable?, String, Float>>.toDataPoints(): List<DataPoint> =
    map {
        DataPoint(
            drawable = it.first,
            value = it.third,
            screenPositionX = 0f,
            screenPositionY = 0f,
            label = it.second
        )
    }
