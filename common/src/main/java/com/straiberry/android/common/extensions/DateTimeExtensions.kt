package com.straiberry.android.common.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Long.plus24Hour():Long{
    return this + 24 * 60 * 60 * 1000
}
fun String.toDate(): Date? {
    val format = SimpleDateFormat(RemoteDateFormatWithThreeMilliSecond, Locale.ENGLISH).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    try {
        return format.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return Date()
}

fun Calendar.getWeekCountOfMonth(){
    val cal = this
    for (i in 0..10) {
        cal[Calendar.YEAR] = 2010
        cal[Calendar.DAY_OF_MONTH] = 1
        cal[Calendar.MONTH] = i
        val maxWeekNumber = cal.getActualMaximum(Calendar.WEEK_OF_MONTH)
    }
}

fun Date.getFirstDayOfMonth(): Date {
    return Calendar.getInstance().apply {
        time = this@getFirstDayOfMonth
        set(Calendar.DAY_OF_MONTH, 1)
    }.time
}

fun Date.getLastDayOfMonth(): Date {
    return Calendar.getInstance().apply {
        time = this@getLastDayOfMonth
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    }.time
}

fun Date.getYearFromDate(): String {
    var year: String
    Calendar.getInstance().apply {
        time = this@getYearFromDate
        year = get(Calendar.YEAR).toString()
    }
    return year
}

fun Date.getAmPmFromDate1(): String {
    var amPm: String
    Calendar.getInstance().apply {
        time = this@getAmPmFromDate1
        amPm = get(Calendar.AM_PM).toString()
    }
    return amPm
}

fun String.getAmPm(): String {
    return if (this.toInt() < 12 || this == "00")
        "PM"
    else
        "AM"
}

fun Date.getWeekFromDate(): String {
    var year: String
    Calendar.getInstance().apply {
        time = this@getWeekFromDate
        year = get(Calendar.WEEK_OF_MONTH).toString()
    }
    return year
}

fun Date.getDayOfWeekFromDate(): String {
    var year: String
    Calendar.getInstance().apply {
        time = this@getDayOfWeekFromDate
        year = get(Calendar.DAY_OF_WEEK).toString()
    }
    return year
}

fun Date.addWeek(numberOfWeeks: Int): Date {
    return Calendar.getInstance().apply {
        time = this@addWeek
        add(Calendar.WEEK_OF_YEAR, numberOfWeeks)
    }.time
}

fun Date.addMonth(numberOfMonth: Int): Date {
    return Calendar.getInstance().apply {
        time = this@addMonth
        add(Calendar.MONTH, numberOfMonth)
    }.time
}

fun Date.addYear(numberOfYears: Int): Date {
    return Calendar.getInstance().apply {
        time = this@addYear
        add(Calendar.YEAR, numberOfYears)
    }.time
}

fun Date.minesYear(numberOfYears: Int): Date {
    return Calendar.getInstance().apply {
        time = this@minesYear
        add(Calendar.YEAR, -numberOfYears)
    }.time
}

fun Date.setTime(hour: Int, minute: Int, am_pm: String): Date {
    return Calendar.getInstance().apply {
        time = this@setTime
        set(Calendar.HOUR, if (hour == 12 && am_pm == "PM") 0 else hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.AM_PM, if (am_pm == "AM") Calendar.AM else Calendar.PM)
    }.time
}

fun Date.changeToAmPm() {
    val dt = Date(this.time)
    val sdf = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
    val time1 = sdf.format(dt)
//    val d = Time.valueOf(time1)
    val df = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).parse(time1)
}

fun Date.changeMonth(month: Int): Date {
    return Calendar.getInstance().apply {
        time = this@changeMonth
        set(Calendar.MONTH, month)
    }.time
}

fun Date.changeYear(year: Int): Date {
    return Calendar.getInstance().apply {
        time = this@changeYear
        set(Calendar.YEAR, year)
    }.time
}

fun Date.getMonthFromDate(): String {
    var month: String
    Calendar.getInstance().apply {
        time = this@getMonthFromDate
        month = SimpleDateFormat("MMM", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }.format(time)
    }
    return month
}

fun Date.getMinuteFromDate(): String {
    var minute: String
    Calendar.getInstance().apply {
        time = this@getMinuteFromDate
        minute = SimpleDateFormat("mm", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }.format(time)
    }
    return minute
}

fun Date.getHourFromDate(): String {
    var hour: String
    Calendar.getInstance().apply {
        time = this@getHourFromDate
        hour = SimpleDateFormat("hh", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }.format(time)
    }
    return hour
}

fun Date.getHourIn24FromDate(): String {
    var hour: String
    Calendar.getInstance().apply {
        time = this@getHourIn24FromDate
        hour = SimpleDateFormat("HH", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }.format(time)
    }
    return hour
}

fun Date.getAMPMFromDate(): String {
    var am_pm: String
    Calendar.getInstance().apply {
        time = this@getAMPMFromDate
        am_pm = SimpleDateFormat("a", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }.format(time)
    }
    return am_pm
}

fun String.getDate(withMilliSecond: Boolean): Calendar {
    return Calendar.getInstance().apply {
        time = if (withMilliSecond)
            SimpleDateFormat(RemoteDateFormatWithMilli, Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(
                this@getDate
            )!!
        else
            SimpleDateFormat(RemoteDateFormat, Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(
                this@getDate
            )!!

    }
}

fun String.getDate(): Calendar {
    return Calendar.getInstance().apply {
        time = SimpleDateFormat(LocalDateFormat, Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(
            this@getDate
        )!!
    }
}

fun String.getDateWithAmPm(): Calendar {
    return Calendar.getInstance().apply {
        time = SimpleDateFormat(LocaleDataFormatWithAmPm, Locale.ENGLISH).parse(
            this@getDateWithAmPm
        )!!
    }
}


fun String.getDateFromOralHygieneData(): Calendar {
    return Calendar.getInstance().apply {
        time = SimpleDateFormat(RemoteDateFormatYear, Locale.ENGLISH).parse(
            this@getDateFromOralHygieneData
        )!!
    }
}

fun String.getDateFromWeeklyBrushingData(): Calendar {
    return Calendar.getInstance().apply {
        time = SimpleDateFormat(RemoteDateFormatYearSecond,Locale.ENGLISH).parse(
            this@getDateFromWeeklyBrushingData
        )!!
    }
}

fun Long.getDataFromMilli(): Calendar {
    val dateString = SimpleDateFormat(RemoteDateFormatYear, Locale.ENGLISH).format(
        Date(this@getDataFromMilli)
    )
    return Calendar.getInstance().apply {
        time = SimpleDateFormat(RemoteDateFormatYear, Locale.ENGLISH).parse(
            dateString
        )!!
    }
}

fun String.convertToAmPmDate(): Date {
    return SimpleDateFormat(LocalDateFormat, Locale.ENGLISH).parse(
        this
    )!!
}

fun Date.getActualMaximum(): Int {
    var actualDayOfCurrentMonth: Int
    Calendar.getInstance().apply {
        time = this@getActualMaximum
        actualDayOfCurrentMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    return actualDayOfCurrentMonth
}

fun Date.getDayOfYear(): Int {
    var dayOfYear: Int
    Calendar.getInstance().apply {
        time = this@getDayOfYear
        dayOfYear = get(Calendar.DAY_OF_YEAR)
    }
    return dayOfYear
}

fun Date.getMonthNumberFromDate(): String {
    var month: String
    Calendar.getInstance().apply {
        time = this@getMonthNumberFromDate
        month = SimpleDateFormat("MM", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }.format(time)
    }
    return month
}

fun Date.getDayFromDate(): String {
    var day: String
    Calendar.getInstance().apply {
        time = this@getDayFromDate
        day = SimpleDateFormat("d", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }.format(time)
    }
    return day
}

fun Date.getTimeFromDate(): String {
    var currentTime: String
    Calendar.getInstance().apply {
        time = this@getTimeFromDate
        currentTime = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }.format(time)
    }
    return currentTime
}

fun Long.getDateWithYearAndMonth(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat(RemoteDateFormatYearSecond, Locale.ENGLISH)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(date)
}

fun Long.getDate(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)
    sdf.timeZone = TimeZone.getTimeZone("GMT-4")
    return sdf.format(date)
}

fun Long.getMonthAndDay(): String? {
    val date = Date(this) // *1000 is to convert seconds to milliseconds
    val sdf = SimpleDateFormat("dd MMM",Locale.ENGLISH) // the format of your date
    sdf.timeZone = TimeZone.getTimeZone("GMT-4")
    return sdf.format(date)
}

const val RemoteDateFormatWithMilli = "yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'"
const val RemoteDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val RemoteDateFormatWithThreeMilliSecond = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val RemoteDateFormatYear = "yyyy/MM/dd"
const val RemoteDateFormatYearSecond = "yyyy-MM-dd"
const val LocaleDataFormatWithAmPm = "MMM dd,yyyy H:mm:ss a"

private const val LocalDateFormat = "EEE MMM dd hh:mm:ss zzz yyyy"

