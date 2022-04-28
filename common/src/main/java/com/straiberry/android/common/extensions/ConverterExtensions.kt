package com.straiberry.android.common.extensions

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.straiberry.android.common.R
import com.straiberry.android.common.model.EventColor
import com.straiberry.android.common.model.EventRepetition
import com.straiberry.android.common.model.JawPosition
import kotlin.math.roundToInt

val Float.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

fun Int.dp(context: Context): Int =
    (this * context.resources.displayMetrics.density).roundToInt()

fun Any?.toStringOrEmpty() = this?.toString() ?: ""
fun Any?.toStringOrEmptyChartData() = this?.toString() ?: "-1"


fun Any?.toStringOrEmptyHorizontalChartData(maximumValue: String): String {
    return when {
        this == null -> {
            "-1"
        }
        this.toString().toFloat() >= maximumValue.toFloat() -> maximumValue
        else -> this.toString()
    }
}

fun Any?.toStringOrZero() = this?.toString() ?: "0"
fun String.getWelcomeAnswersValue(): String {
    return if (this == "")
        "-2"
    else
        (this.toInt() + 1).toString()
}

fun String.toStringOrEmptyValue(): String {
    return if (this == "")
        "-2"
    else
        this
}

fun Double.toImageXPosition(width: Int): Int {
    return (this * width).toInt()
}

fun Double.toImageYPosition(height: Int): Int {
    return (this * height).toInt()
}


fun Int.convertToCheckupName(context: Context): String {
    return when (this) {
        -1 -> context.getString(R.string.regular_checkup)
        0 -> context.getString(R.string.teeth_whitening)
        1 -> context.getString(R.string.toothache_amp_tooth_sensitivity)
        2 -> context.getString(R.string.problems_with_previous_treatment)
        3 -> context.getString(R.string.others)
        else -> context.getString(R.string.regular_checkup)
    }
}

fun ArrayList<Pair<Int, Boolean>>.extractYesNoAnswers(index: Int): String? {
    var answer: String? = null
    this.forEach {
        if (it.first == index)
            answer = if (it.second)
                "0"
            else
                "-1"
    }
    return answer
}

fun String.getNextOverallScore(): String {
    return when (this) {
        "A+" -> "A+"
        "A" -> "A+"
        "A-" -> "A"
        "B+" -> "A-"
        "B" -> "B+"
        "B-" -> "B"
        "C+" -> "B-"
        "C" -> "C+"
        "C-" -> "C"
        "D+" -> "C-"
        "D" -> "D+"
        "D-" -> "D"
        else -> ""
    }
}

fun String.getPreviousOverallScore(): String {
    return when (this) {
        "A+" -> "A"
        "A" -> "A-"
        "A-" -> "B+"
        "B+" -> "B"
        "B" -> "B-"
        "B-" -> "C+"
        "C+" -> "C"
        "C" -> "C-"
        "C-" -> "D+"
        "D+" -> "D"
        "D" -> "D-"
        "D-" -> "D-"
        else -> ""
    }
}

fun String.convertToBoolean(): Boolean {
    return this == "0"
}

fun EventRepetition.convertToInt(): Int {
    return when (this) {
        EventRepetition.None -> 0
        EventRepetition.Daily -> 1
        EventRepetition.Weekly -> 2
        EventRepetition.Biweekly -> 3
        EventRepetition.Monthly -> 4
        EventRepetition.EveryThreeMonth -> 5
        EventRepetition.EverySixMonth -> 6
        EventRepetition.Yearly -> 7
    }
}

fun EventColor.convertToInt(): Int {
    return when (this) {
        EventColor.Green -> 0
        EventColor.Blue -> 1
        EventColor.BlueGray -> 2
        EventColor.GrayBlack -> 3
        EventColor.Orange -> 4
        EventColor.Yellow -> 5
        else -> 0
    }
}

fun String.convertToEventColor(): EventColor {
    return when (this) {
        "0" -> EventColor.Green
        "1" -> EventColor.Blue
        "2" -> EventColor.BlueGray
        "3" -> EventColor.GrayBlack
        "4" -> EventColor.Orange
        "5" -> EventColor.Yellow
        else -> EventColor.Empty
    }
}

fun String.convertToRepetition(): EventRepetition {
    return when (this) {
        "0" -> EventRepetition.None
        "1" -> EventRepetition.Daily
        "2" -> EventRepetition.Weekly
        "3" -> EventRepetition.Biweekly
        "4" -> EventRepetition.Monthly
        "5" -> EventRepetition.EveryThreeMonth
        "6" -> EventRepetition.EverySixMonth
        "7" -> EventRepetition.Yearly
        else -> EventRepetition.None
    }
}

fun EventColor.convertToResourceColor(context: Context): Int {
    val nightModeFlags: Int = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
    return when (this) {
        EventColor.Blue -> R.color.green700
        EventColor.Green -> R.color.green200
        EventColor.BlueGray -> R.color.primaryLight200
        EventColor.Orange -> R.color.orange500
        EventColor.Yellow -> R.color.yellow200
        EventColor.Empty ->
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
                R.color.secondaryDark
            else
                R.color.secondaryLight
        EventColor.GrayBlack -> R.color.bodyText500
    }
}

fun String.convertJawToInt(): Int {
    return when (this) {
        "front" -> 1
        "upper" -> -1
        "lower" -> 0
        else -> 1
    }
}

fun Int.convertIntToJaw(): String {
    return when (this) {
        1 -> "front"
        -1 -> "upper"
        0 -> "lower"
        else -> "Over"
    }
}

@Throws(NullPointerException::class)
fun Int.convertToothIdToDental(): String {
    return when (this) {
        // Upper right
        0 -> "ur8"
        1 -> "ur7"
        2 -> "ur6"
        3 -> "ur5"
        4 -> "ur4"
        5 -> "ur3"
        6 -> "ur2"
        7 -> "ur1"
        // Upper left
        8 -> "ul1"
        9 -> "ul2"
        10 -> "ul3"
        11 -> "ul4"
        12 -> "ul5"
        13 -> "ul6"
        14 -> "ul7"
        15 -> "ul8"
        // Lower right
        16 -> "lr8"
        17 -> "lr7"
        18 -> "lr6"
        19 -> "lr5"
        20 -> "lr4"
        21 -> "lr3"
        22 -> "lr2"
        23 -> "lr1"
        // Lower left
        24 -> "ll1"
        25 -> "ll2"
        26 -> "ll3"
        27 -> "ll4"
        28 -> "ll5"
        29 -> "ll6"
        30 -> "ll7"
        31 -> "ll8"
        else -> throw NullPointerException("None of inputs match the converter")
    }
}


@Throws(NullPointerException::class)
fun String.convertDentalToToothId(): Int {
    return when (this) {
        // Upper Right
        "ur8" -> 0
        "ur7" -> 1
        "ur6" -> 2
        "ur5" -> 3
        "ur4" -> 4
        "ur3" -> 5
        "ur2" -> 6
        "ur1" -> 7
        // Upper Left
        "ul1" -> 8
        "ul2" -> 9
        "ul3" -> 10
        "ul4" -> 11
        "ul5" -> 12
        "ul6" -> 13
        "ul7" -> 14
        "ul8" -> 15
        // Lower Right
        "lr8" -> 16
        "lr7" -> 17
        "lr6" -> 18
        "lr5" -> 19
        "lr4" -> 20
        "lr3" -> 21
        "lr2" -> 22
        "lr1" -> 23
        // Lower Left
        "ll1" -> 24
        "ll2" -> 25
        "ll3" -> 26
        "ll4" -> 27
        "ll5" -> 28
        "ll6" -> 29
        "ll7" -> 30
        "ll8" -> 31
        else -> throw NullPointerException("None of inputs match the converter")
    }
}

fun Int.convertCavityClassToDrawable(context: Context): Drawable {
    return when (this) {
        0 -> ContextCompat.getDrawable(context, R.drawable.ic_amalgam_filling)!!
        1 -> ContextCompat.getDrawable(context, R.drawable.ic_calcules)!!
        2 -> ContextCompat.getDrawable(context, R.drawable.ic_carries)!!
        3 -> ContextCompat.getDrawable(context, R.drawable.ic_white_spot)!!
        else -> ContextCompat.getDrawable(context, R.drawable.ic_amalgam_filling)!!
    }
}

fun Int.convertCavityClassToString(context: Context): String {
    return when (this) {
        0 -> context.getString(R.string.amalgam_filling)
        1 -> context.getString(R.string.calculus)
        2 -> context.getString(R.string.carries)
        3 -> context.getString(R.string.white_spot)
        else -> context.getString(R.string.amalgam_filling)
    }
}

fun List<String>.convertToToothId(): ArrayList<Int> {
    val listOfToothId = arrayListOf<Int>()
    this.forEach {
        listOfToothId.add(it.convertDentalToToothId())
    }
    return listOfToothId
}

fun Int.convertToothClassToFrontJawPosition(): JawPosition {
    return if (this in 0..15)
        JawPosition.FrontTeethUpper
    else
        JawPosition.FrontTeethLower
}

fun Int.convertToJawPosition(): JawPosition {
    return when (this) {
        1 -> JawPosition.FrontTeeth
        -1 -> JawPosition.UpperJaw
        0 -> JawPosition.LowerJaw
        else -> JawPosition.FrontTeeth
    }
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




