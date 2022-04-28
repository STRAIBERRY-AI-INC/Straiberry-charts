package com.straiberry.android.charts.extenstions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.straiberry.android.charts.R
import com.straiberry.android.common.extensions.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


private fun resizeAvatarForCharts(context: Context, image: Drawable?): Drawable? {
    val b = (image as BitmapDrawable).bitmap
    return try {
        val bitmapResized = Bitmap.createScaledBitmap(b, 40, 40, false)
        BitmapDrawable(context.resources, bitmapResized)
    } catch (e: java.lang.Exception) {
        null
    }
}

data class HorizontalChartXLabels(
    val xFirstDrawable: Drawable,
    val xFirstTitle: String,
    val xSecondDrawable: Drawable,
    val xSecondTitle: String,
    val xThirdDrawable: Drawable,
    val xThirdTitle: String,
    val xFourDrawable: Drawable,
    val xFourTitle: String,
    val xFiveDrawable: Drawable,
    val xFiveTitle: String,
    val xSixDrawable: Drawable,
    val xSixTitle: String
)

fun horizontalChartData(
    context: Context,
    horizontalChartXLabels: HorizontalChartXLabels,
    listOfScores: List<Float> = listOf(-1F, -1F, -1F, -1F, -1F, -1F, -1F),
): List<Triple<Drawable?, String, Float>> =
    listOf(
        Triple(
            horizontalChartXLabels.xSixDrawable,
            horizontalChartXLabels.xSixTitle,
            listOfScores[5]
        ),
        Triple(
            horizontalChartXLabels.xFiveDrawable,
            horizontalChartXLabels.xFiveTitle,
            listOfScores[4]
        ),
        Triple(
            horizontalChartXLabels.xFourDrawable,
            horizontalChartXLabels.xFourTitle,
            listOfScores[3]
        ),
        Triple(
            horizontalChartXLabels.xThirdDrawable,
            horizontalChartXLabels.xThirdTitle,
            listOfScores[2]
        ),
        Triple(
            horizontalChartXLabels.xSecondDrawable,
            horizontalChartXLabels.xSecondTitle,
            listOfScores[1]
        ),
        Triple(
            try {
                resizeAvatarForCharts(context, horizontalChartXLabels.xFirstDrawable)
                    ?: ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_empty_avatar_small
                    )
            } catch (e: java.lang.Exception) {
                horizontalChartXLabels.xFirstDrawable
            },
            horizontalChartXLabels.xFirstTitle,
            listOfScores[0]
        )
    )


fun ArrayList<HashMap<String, Int?>>.toChartData(): List<Triple<Drawable?, String, Float>> {
    val currentTime = System.currentTimeMillis()

    return listOf(
        Triple(
            null,
            (currentTime - TimeUnit.DAYS.toMillis(SixDay)).getDate(),
            try {
                this.getValues((currentTime - TimeUnit.DAYS.toMillis(SixDay)))
            } catch (e: Exception) {
                EmptyChartData
            }
        ),
        Triple(
            null,
            (currentTime - TimeUnit.DAYS.toMillis(FiveDay)).getDate(),
            try {
                this.getValues((currentTime - TimeUnit.DAYS.toMillis(FiveDay)))
            } catch (e: Exception) {
                EmptyChartData
            }
        ),
        Triple(
            null,
            (currentTime - TimeUnit.DAYS.toMillis(FourDay)).getDate(),
            try {
                this.getValues((currentTime - TimeUnit.DAYS.toMillis(FourDay)))
            } catch (e: Exception) {
                EmptyChartData
            }
        ),
        Triple(
            null,
            (currentTime - TimeUnit.DAYS.toMillis(ThreeDay)).getDate(),
            try {
                this.getValues((currentTime - TimeUnit.DAYS.toMillis(ThreeDay)))
            } catch (e: Exception) {
                EmptyChartData
            }
        ),
        Triple(
            null,
            (currentTime - TimeUnit.DAYS.toMillis(TwoDay)).getDate(),
            try {
                this.getValues((currentTime - TimeUnit.DAYS.toMillis(TwoDay)))
            } catch (e: Exception) {
                EmptyChartData
            }
        ),
        Triple(
            null,
            (currentTime - TimeUnit.DAYS.toMillis(OneDay)).getDate(),
            try {
                this.getValues((currentTime - TimeUnit.DAYS.toMillis(OneDay)))
            } catch (e: Exception) {
                EmptyChartData
            }
        ),
        Triple(
            null,
            "",
            try {
                this.getValues((currentTime))
            } catch (e: Exception) {
                EmptyChartData
            }
        )
    )
}

fun String.convertScoreToChartValue(): Int {
    return when (this) {
        "D-" -> 1
        "D" -> 2
        "D+" -> 3
        "C-" -> 4
        "C" -> 5
        "C+" -> 6
        "B-" -> 7
        "B" -> 8
        "B+" -> 9
        "A-" -> 10
        "A" -> 11
        "A+" -> 12
        else -> 0
    }
}

fun Int.convertToChartScore(): String {
    return when (this) {
        1 -> "D-"
        2 -> "D"
        3 -> "D+"
        4 -> "C-"
        5 -> "C"
        6 -> "C+"
        7 -> "B-"
        8 -> "B"
        9 -> "B+"
        10 -> "A-"
        11 -> "A"
        12 -> "A+"
        else -> "0"
    }
}

val arrayListOfMonthDate = Array(30) { index ->
    (System.currentTimeMillis() - TimeUnit.DAYS.toMillis(index.toLong())).toString()
}

fun HashMap<String, List<String?>>.getListOfValues(day: Int): List<String?> {
    val values: List<String?> = try {
        val listOfCurrentDayValues = this@getListOfValues.filterKeys {
            it.getDateFromOralHygieneData().time.getDayFromDate() == arrayListOfMonthDate[day].toLong()
                .getDataFromMilli().time.getDayFromDate()
        }.values.first().filterNotNull()
        if (listOfCurrentDayValues.isEmpty())
            arrayListOf(null)
        else
            listOfCurrentDayValues
    } catch (e: Exception) {
        arrayListOf(null)
    }
    return values
}

fun List<HashMap<String, Int?>>.getValues(currentTime: Long): Float {
    var values: Float? = -1f
    var valueKey = ""
    this.forEachIndexed { index, hashMap ->
        hashMap.forEach {
            if (it.key.getDateFromWeeklyBrushingData().time.getDayFromDate() == currentTime.getDataFromMilli().time.getDayFromDate())
                values = it.value!!.toFloat()
        }
    }

    return values!!
}

fun HashMap<String, List<String?>>.toOralHygieneChart(): ArrayList<LineChartValue> {
    val arrayListOfMonthDate = Array(30) { index ->
        (System.currentTimeMillis() - TimeUnit.DAYS.toMillis(index.toLong())).toString()
    }

    return ArrayList<LineChartValue>().apply {
        add(
            LineChartValue(
                Array(30) {
                    Value(
                        arrayListOfMonthDate[it],
                        this@toOralHygieneChart.getListOfValues(it)
                    )
                }.reversedArray()
            )
        )
    }
}

fun Long.getMonthAndDay(): String? {
    val date = Date(this) // *1000 is to convert seconds to milliseconds
    val sdf = SimpleDateFormat("dd MMM", Locale.ENGLISH) // the format of your date
    sdf.timeZone = TimeZone.getTimeZone("GMT-4")
    return sdf.format(date)
}

fun Int.convertToOralHygieneScore(): String {
    return when {
        this <= -26 -> "D-"
        this <= -21 -> "D"
        this <= -16 -> "D+"
        this <= -11 -> "C-"
        this <= -6 -> "C"
        this <= -1 -> "C+"
        this <= 4 -> "B-"
        this <= 9 -> "B"
        this <= 14 -> "B+"
        this <= 19 -> "A-"
        this <= 24 -> "A"
        this <= 30 -> "A+"
        else -> ""
    }
}

fun Int.converterOralHygieneScore(): Int {
    return when {
        this <= -26 -> 1
        this <= -21 -> 2
        this <= -16 -> 3
        this <= -11 -> 4
        this <= -6 -> 5
        this <= -1 -> 6
        this <= 4 -> 7
        this <= 9 -> 8
        this <= 14 -> 9
        this <= 19 -> 10
        this <= 24 -> 11
        this <= 30 -> 12
        else -> 0
    }
}

fun ArrayList<LineChartValue>.convertToLinearChartData(): ArrayList<Triple<Drawable?, String, Float>> {
    val data: ArrayList<Triple<Drawable?, String, Float>> = ArrayList()
    this.first().value.forEachIndexed { index, value ->
        var score = 0
        if (value.score.size > 1) {
            value.score.forEach {
                score += it!!.toInt() ?: 0
            }
            score /= value.score.size
        } else {
            score = if (value.score.first() != null) value.score.first()!!
                .toInt() else 0
        }
        data.add(
            Triple(
                null,
                value.date.toLong().getMonthAndDay()!!,
                if (score == 0) 0f else score.toFloat()
            )
        )
    }
    return data
}

data class LineChartValue(val value: Array<Value>)
data class Value(val date: String, val score: List<String?>)

private const val EmptyChartData = -1f
private const val OneDay = 1L
private const val TwoDay = 2L
private const val ThreeDay = 3L
private const val FourDay = 4L
private const val FiveDay = 5L
private const val SixDay = 6L


