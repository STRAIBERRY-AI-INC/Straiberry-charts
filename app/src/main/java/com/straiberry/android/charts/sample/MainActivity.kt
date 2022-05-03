package com.straiberry.android.charts.sample

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.straiberry.android.charts.extenstions.*
import com.straiberry.android.charts.sample.databinding.ActivityMainBinding
import com.straiberry.android.charts.tooltip.PointTooltip
import com.straiberry.android.charts.tooltip.SliderTooltip
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sliderTooltip: SliderTooltip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sliderTooltip = SliderTooltip()
        setupLinearChart()
        setupBarPercentChart()
        setupHorizontalChart()
        setupBarChart()
    }

    private fun setupBarPercentChart() {
        // Prepare the tooltip to show on chart
        val pointTooltip = PointTooltip()
        pointTooltip.onCreateTooltip(binding.content)
        binding.barPercentChartWhitening.apply {
            tooltip = pointTooltip
            currentAverage = 20
            average = 50
            previousAverage = 30
            createBarPercent()
            disableTouchAndClick()
        }
    }

    private fun setupBarChart() {
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        val data: ArrayList<HashMap<String, Int?>> = arrayListOf()

        repeat(7){
            data.add(hashMapOf(Pair(calendar.time.convertCurrentDateToChartDate(LINE_CHART_DATE_FORMAT),it+1)))
            calendar.add(Calendar.DATE, -1)
        }

        binding.barChartViewBrushing.animate(data.toChartData())
    }

    /** Setup data for whitening horizontal chart */
    private fun setupHorizontalChart() {

        val data = horizontalChartData(
            this, HorizontalChartXLabels(
                ContextCompat.getDrawable(this, R.drawable.ic_user)!!, "user",
                ContextCompat.getDrawable(this, R.drawable.ic_user)!!, "user",
                ContextCompat.getDrawable(this, R.drawable.ic_age)!!, "age",
                ContextCompat.getDrawable(this, R.drawable.ic_gender)!!, "gender",
                ContextCompat.getDrawable(this, R.drawable.ic_location)!!, "location",
                ContextCompat.getDrawable(this, R.drawable.ic_master)!!, "master",
            ), listOf(6F, 7F, 2F, 7F, 7F, 5F)
        )
        binding.horizontalBarChartViewBrushingCharacter.animate(data)
        binding.horizontalBarChartViewBrushingDigit.animate(data)
    }


    private fun setupLinearChart() {
        // Chart data
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        //Create a fake data for one month
        val remoteData = HashMap<String, List<String?>>()
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("11")
        calendar.add(Calendar.DATE, -1)
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("11")
        calendar.add(Calendar.DATE, -1)
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("11")
        calendar.add(Calendar.DATE, -1)
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("10" )
        calendar.add(Calendar.DATE, -1)
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("11")
        calendar.add(Calendar.DATE, -1)
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("7")
        calendar.add(Calendar.DATE, -1)
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("9")
        calendar.add(Calendar.DATE, -1)
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("4")
        calendar.add(Calendar.DATE, -1)
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("5")
        calendar.add(Calendar.DATE, -1)
        remoteData[calendar.time.convertCurrentDateToChartDate(LINEAR_CHART_DATE_FORMAT)] = listOf("6")

        // Convert data to chart input
        val lineChartData = remoteData.toOralHygieneChart()
        val convertedData = lineChartData.convertToLinearChartData()
        // Setup tooltip and chart color
        // In the triple data:
        // first = Drawable
        // second = date
        // third = score
        var isDataFake = false
        sliderTooltip.apply {
            // Get the first date that has a value
            val isDataSizeIsOne = convertedData.filter { it.third != 0.0f }.size == 1
            val firstData = convertedData.reversed().firstOrNull { it.third != 0.0f }
            if (isDataSizeIsOne && convertedData.reversed().first().third == 0.0f) {
                isDataFake = true
                convertedData[convertedData.size - 1] = Triple(
                    ColorDrawable(Color.TRANSPARENT),
                    firstData!!.second,
                    firstData.third
                )
            }
            var firstChartData: Value? = null
            var scoreAverage = ""
            var date = ""
            if (firstData != null) {
                // Get first score average
                scoreAverage = firstData.third.toInt().convertToChartScore()
                // Get first date
                date = firstData.second
                // Get all score if score are more then one
                firstChartData = lineChartData.first().value.reversed().firstOrNull {
                    it.score.size > 1 && it.date.toLong().getMonthAndDay() == firstData.second
                }
            }
            // Set the first two score to show on screen
            var scoreOne = ""
            var scoreTwo = ""
            if (firstChartData != null) {
                scoreOne = firstChartData.score[0].toString()
                scoreTwo = firstChartData.score[1].toString()
            }
            // Set tooltip data
            this.scoreAverage = scoreAverage
            this.date = date
            if (scoreOne != "")
                this.scoreOne = scoreOne
            if (scoreTwo != "")
                this.scoreTwo = scoreTwo
        }

        binding.linearChartViewOralHygiene.apply {
            isTooltipDraw = false
            tooltip = sliderTooltip
            isFake = isDataFake
            // Change colors of chart
            gradientFillColors = intArrayOf(
                ContextCompat.getColor(this@MainActivity, R.color.primaryLight200),
                ContextCompat.getColor(this@MainActivity, R.color.secondaryLight)
            )
            lineColor = ContextCompat.getColor(this@MainActivity, R.color.white)
            // Set animation duration to draw the chart
            animation.duration = CHART_ANIMATION_DURATION
        }

        // If you are using view pager you can disable input touch to avoid
        // getting conflict with chart touch
//        binding.linearChartViewOralHygiene.onDataPointUnTouchListener = { _, _, _ ->
//            FragmentHome.viewPager2.isUserInputEnabled = true
//        }
        binding.linearChartViewOralHygiene.onDataPointTouchListener = { index, _, _ ->
//            FragmentHome.viewPager2.isUserInputEnabled = false
            val isFake = convertedData[index].first != null
            val scoreAverage = convertedData[index].third.toInt().convertToChartScore()
            val date = convertedData[index].second
            var scoreOne = ""
            var scoreTwo = ""
            if (lineChartData.first().value[index].score.size > 1) {
                scoreOne = lineChartData.first().value[index].score[0].toString()
                scoreTwo = lineChartData.first().value[index].score[1].toString()
            }
            binding.linearChartViewOralHygiene.tooltip = sliderTooltip.apply {
                this.isFake = isFake
                this.scoreAverage = scoreAverage
                this.date = date
                if (scoreOne != "") {
                    this.scoreOne = scoreOne
                }

                this.scoreTwo = scoreTwo
            }
        }

        binding.linearChartViewOralHygiene.animate(convertedData)
    }

    private fun Date.convertCurrentDateToChartDate(dataFormat:String): String {
        var day: String
        Calendar.getInstance().apply {
            time = this@convertCurrentDateToChartDate
            day = SimpleDateFormat(dataFormat, Locale.ENGLISH).apply {
                timeZone = TimeZone.getDefault()
            }.format(time)
        }
        return day
    }

    companion object {
        private const val LINEAR_CHART_DATE_FORMAT = "yyyy/MM/dd"
        private const val LINE_CHART_DATE_FORMAT = "yyyy-MM-dd"

        private const val CHART_ANIMATION_DURATION = 2500L
    }
}