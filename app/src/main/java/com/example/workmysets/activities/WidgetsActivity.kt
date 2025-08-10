package com.example.workmysets.activities

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.example.workmysets.R
import com.example.workmysets.data.entities.session.entity.SessionWithExercise
import com.example.workmysets.data.viewmodels.SessionViewModel
import com.example.workmysets.databinding.ActivityWidgetsBinding
import com.example.workmysets.utils.AppLocale
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

data class WeekGroup(
    val weekYear: String,
    val sessions: List<SessionWithExercise>,
    val month: Month // the month of the week’s start date (Monday)
)

class WidgetsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWidgetsBinding
    private val animatedViews = mutableSetOf<View>()

    private lateinit var sessionViewModel: SessionViewModel

    private lateinit var allSessions: List<SessionWithExercise>

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
    private val formatterWithoutMinutes: DateTimeFormatter =
        DateTimeFormatter.ofPattern("hh:00 a", Locale.US)

    private val today: LocalDate = LocalDate.now(AppLocale.ZONE)
    private val startOfThisWeek: LocalDate = today.with(DayOfWeek.MONDAY)
    private val endOfThisWeek: LocalDate = today.with(DayOfWeek.SUNDAY)
    private val startOfLastWeek: LocalDate = startOfThisWeek.minusWeeks(1)
    private val endOfLastWeek: LocalDate = startOfThisWeek.minusDays(1) // Sunday of last week

    private lateinit var lastWeekSessions: List<SessionWithExercise>
    private lateinit var currentWeekSessions: List<SessionWithExercise>

    private val neutralTextColor = Color.parseColor("#B0B0B0")
    private val neutralGridColor = Color.parseColor("#808080")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWidgetsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionViewModel = ViewModelProvider(this)[SessionViewModel::class]

        binding.topBar.titleText.text = "Stats"
        binding.topBar.backButton.visibility = View.VISIBLE
        binding.topBar.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.topBar.actionButton.visibility = View.VISIBLE
        binding.topBar.actionButton.isClickable = false
        binding.topBar.actionButton.isFocusable = false
        binding.topBar.actionButton.setImageDrawable(getDrawable(R.drawable.ic_bar_chart))

        sessionViewModel.allSessions.observe(this) {
            binding.loadingOverlay.visibility = View.GONE

            allSessions = it

            currentWeekSessions = allSessions.filter {
                val date = Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE).toLocalDate()
                date in startOfThisWeek..endOfThisWeek
            }

            lastWeekSessions = allSessions.filter {
                val date = Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE).toLocalDate()
                date in startOfLastWeek..endOfLastWeek
            }

            populateAverageTimeCard()
            populateSetsCard()
            populateHoursSpentCard()
            populateRestTimeCard()
            populateWeightsUsedCard()
            setupScrollAnimations()
        }
    }

    private fun groupSessionsByWeek(sessions: List<SessionWithExercise>): List<WeekGroup> {
        val sessionsByDate = sessions.map {
            val date = Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE).toLocalDate()
            date to it
        }

        val weekFormatter = DateTimeFormatter.ofPattern("YYYY-'W'ww") // e.g., 2025-W32

        return sessionsByDate.groupBy { (date, _) ->
            // Get the week start and format as "YYYY-Www"
            val weekStart = date.with(DayOfWeek.MONDAY)
            weekStart.format(weekFormatter)
        }.map { (weekYear, entries) ->
            val weekStart = entries.first().first.with(DayOfWeek.MONDAY)
            WeekGroup(
                weekYear = weekYear, sessions = entries.map { it.second }, month = weekStart.month
            )
        }.sortedBy { it.weekYear } // sorts by week chronologically
    }

    /**
     * Formats a total number of seconds into a human-readable string like "1h 2m" or "45s".
     */
    private fun formatSeconds(totalSeconds: Long): String {
        if (totalSeconds <= 0) return "0s"
        val duration = Duration.ofSeconds(totalSeconds)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            // Only show seconds if total duration is less than an hour
            if (hours == 0L && seconds > 0) {
                append("${seconds}s")
            }
        }.trim().ifEmpty { "0s" }
    }

    private fun populateAverageTimeCard() {

        fun getAverageLocalTime(sessions: List<SessionWithExercise>): LocalTime {
            if (sessions.isEmpty()) return LocalTime.MIDNIGHT
            val avg = sessions.map {
                Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE).toLocalTime()
                    .toSecondOfDay()
            }.average().toLong()

            return LocalTime.ofSecondOfDay(avg)
        }

        val averageTime = getAverageLocalTime(allSessions)
        binding.averageWorkoutTime.text = averageTime.format(formatterWithoutMinutes)

        val averageTimeLastWeek =
            if (lastWeekSessions.isNotEmpty()) getAverageLocalTime(lastWeekSessions).format(
                formatterWithoutMinutes
            )
            else "N/A"

        binding.averageWorkoutTimeCompare.text = "Last weeks ~ $averageTimeLastWeek"

        val dataEntries = (0..6).map { dayIndex ->
            val daySessions = currentWeekSessions.filter {
                val start = Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE).toLocalDate()
                start.dayOfWeek.value == dayIndex + 1
            }

            val averageTimeOfDay = getAverageLocalTime(daySessions)
            if (daySessions.isEmpty()) null else Entry(
                dayIndex.toFloat(), averageTimeOfDay.toSecondOfDay().toFloat()
            )
        }.filterNotNull()

        val xAxisLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        val timeValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return LocalTime.ofSecondOfDay(value.toLong()).format(formatterWithoutMinutes)
            }

            override fun getPointLabel(entry: Entry?): String {
                val seconds = entry?.y?.toLong() ?: return ""
                return if (seconds == 0L) "No Data"
                else LocalTime.ofSecondOfDay(seconds).format(formatter)
            }
        }

        val yMin = LocalTime.of(5, 0).toSecondOfDay().toFloat()     // 5:00 AM
        val yMax = LocalTime.of(23, 0).toSecondOfDay().toFloat()    // 11:00 PM

        setupLineChartData(
            dataEntries = dataEntries,
            chart = binding.averageTimeChart,
            label = "Workout Time",
            chartColor = Color.CYAN,
            xAxisLabels = xAxisLabels,
            configureChart = {
                axisLeft.valueFormatter = timeValueFormatter
                axisLeft.axisMinimum = yMin
                axisLeft.axisMaximum = yMax

                xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
            },
            configureDataset = {
                valueFormatter = timeValueFormatter
            })
    }

    private fun populateSetsCard() {

        val groupedSessions = groupSessionsByWeek(allSessions)

        val totalSetsAllTime =
            allSessions.sumOf { it.session.repsPerSet.size }
        binding.totalSets.text = totalSetsAllTime.toString()

        val totalSetsToday = allSessions.filter {
            val date = Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE).toLocalDate()
            date == today
        }.sumOf { it.session.repsPerSet.size }
        binding.totalSetsToday.text = totalSetsToday.toString()

        val currentWeekTotalSets = currentWeekSessions.sumOf { it.session.repsPerSet.size }
        val lastWeekTotalSets = lastWeekSessions.sumOf { it.session.repsPerSet.size }

        val difference = currentWeekTotalSets - lastWeekTotalSets

        val percentageChange = if (lastWeekTotalSets != 0) {
            (difference.toDouble() / lastWeekTotalSets) * 100
        } else {
            if (currentWeekTotalSets > 0) 100.0 else 0.0 // Avoid division by zero
        }

        val description = when {
            percentageChange > 0 -> String.format("%.0f%% more", percentageChange)
            percentageChange < 0 -> String.format("%.0f%% less", -percentageChange)
            else -> "the same"
        }

        binding.totalSetsCompare.text = "You did $description sets compared to last week."

        val averageSets =
            if (allSessions.isNotEmpty()) allSessions.map { it.session.repsPerSet.size }
                .average() else 0.0
        binding.averageSets.text = String.format("%.1f sets", averageSets)

        val data = groupedSessions.mapIndexed { index, it ->
            val weekSets = it.sessions.sumOf { it.session.repsPerSet.size }
            BarEntry(index.toFloat(), weekSets.toFloat())
        }

        val xAxisLabels = groupedSessions.mapIndexed { index, _ -> "Week ${index + 1}" }

        setupBarChartData(
            dataEntries = data,
            chart = binding.setsWidgetChart,
            label = "Weekly Sets",
            xAxisLabels = xAxisLabels,
        )
    }

    private fun populateHoursSpentCard() {

        val hoursSpentOnExercise = allSessions.groupBy { it.exercise.name }
            .mapValues {
                it.value.sumOf { sessions ->
                    sessions.session.setsTimestamp.sumOf { timestamp ->
                        val start = Instant.parse(timestamp.start)
                        val end = Instant.parse(timestamp.end)
                        Duration.between(start, end).toMinutes()
                    }
                }
            }.toMutableMap()

        val totalMinutesSpent = hoursSpentOnExercise.values.sum()
        val averageExerciseDuration =
            if (allSessions.isNotEmpty()) totalMinutesSpent.toDouble() / allSessions.size else 0.0

        fun formatMinutes(minutes: Long): String {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            return buildString {
                if (hours > 0) append("${hours}h ")
                append("${remainingMinutes}m")
            }.trim().ifEmpty { "0m" }
        }

        val totalMinutesSpentToday = allSessions.filter {
            val date = Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE).toLocalDate()
            date == today
        }.sumOf {
            it.session.setsTimestamp.sumOf { timestamp ->
                val start = Instant.parse(timestamp.start)
                val end = Instant.parse(timestamp.end)
                Duration.between(start, end).toMinutes()
            }
        }

        val totalMinutesSpentCurrentWeek = currentWeekSessions.sumOf {
            it.session.setsTimestamp.sumOf { timestamp ->
                val start = Instant.parse(timestamp.start)
                val end = Instant.parse(timestamp.end)
                Duration.between(start, end).toMinutes()
            }
        }
        val totalMinutesSpentLastWeek = lastWeekSessions.sumOf {
            it.session.setsTimestamp.sumOf { timestamp ->
                val start = Instant.parse(timestamp.start)
                val end = Instant.parse(timestamp.end)
                Duration.between(start, end).toMinutes()
            }
        }

        val difference = totalMinutesSpentCurrentWeek - totalMinutesSpentLastWeek

        val percentageChange = if (totalMinutesSpentLastWeek != 0L) {
            (difference.toDouble() / totalMinutesSpentLastWeek) * 100
        } else {
            if (totalMinutesSpentCurrentWeek > 0) 100.0 else 0.0
        }

        val description = when {
            percentageChange > 0 -> String.format("%.0f%% more", percentageChange)
            percentageChange < 0 -> String.format("%.0f%% less", -percentageChange)
            else -> "the same"
        }

        binding.totalHoursSpent.text = formatMinutes(totalMinutesSpent)
        binding.hoursSpentToday.text = formatMinutes(totalMinutesSpentToday)
        binding.hoursSpentCompare.text = "You spent $description time compared to last week."
        binding.averageDuration.text = formatMinutes(averageExerciseDuration.toLong())

        val data: List<PieEntry>
        if (totalMinutesSpent == 0L) {
            data = listOf(PieEntry(100f, "No Data"))
        } else {
            val topExercises = hoursSpentOnExercise.entries.sortedByDescending { it.value }.take(3)
                .associate { it.key to it.value }.toMutableMap()
            val otherMinutes = totalMinutesSpent - topExercises.values.sum()
            if (otherMinutes > 0) {
                topExercises["Other"] = otherMinutes
            }
            data = topExercises.map { (key, value) ->
                PieEntry(value.toFloat(), key)
            }
        }

        setupPieChartData(
            dataEntries = data,
            chart = binding.hoursSpentChart,
            label = "Time Allocation"
        )
    }

    private fun populateRestTimeCard() {
        val totalRestSeconds = allSessions.sumOf { it.session.restsPerSet.sum() }.toLong()
        binding.totalRestTime.text = formatSeconds(totalRestSeconds)

        val restTimeTodaySeconds = allSessions.filter {
            val date = Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE).toLocalDate()
            date == today
        }.sumOf { it.session.restsPerSet.sum() }.toLong()
        binding.restTimeToday.text = formatSeconds(restTimeTodaySeconds)

        val allRests = allSessions.flatMap { it.session.restsPerSet }
        val averageRestSeconds = if (allRests.isNotEmpty()) allRests.average().toLong() else 0L
        binding.averageRestTime.text = formatSeconds(averageRestSeconds)

        // Calculate comparison with last week
        val totalRestThisWeek = currentWeekSessions.sumOf { it.session.restsPerSet.sum() }.toLong()
        val totalRestLastWeek = lastWeekSessions.sumOf { it.session.restsPerSet.sum() }.toLong()

        val difference = totalRestThisWeek - totalRestLastWeek
        val percentageChange = if (totalRestLastWeek != 0L) {
            (difference.toDouble() / totalRestLastWeek) * 100
        } else {
            if (totalRestThisWeek > 0) 100.0 else 0.0 // Avoid division by zero
        }

        val description = when {
            percentageChange > 0 -> String.format("%.0f%% more", percentageChange)
            percentageChange < 0 -> String.format("%.0f%% less", -percentageChange)
            else -> "the same"
        }
        binding.restTimeCompare.text = "You rested for $description time compared to last week."

        // Prepare chart data
        val dataEntries = (0..6).map { dayIndex ->
            val dayOfWeek = DayOfWeek.of(dayIndex + 1)
            val dailyTotalRest = currentWeekSessions.filter {
                Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE)
                    .toLocalDate().dayOfWeek == dayOfWeek
            }.sumOf { it.session.restsPerSet.sum() }.toFloat()

            Entry(dayIndex.toFloat(), dailyTotalRest)
        }

        val xAxisLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        setupLineChartData(
            dataEntries = dataEntries,
            chart = binding.restTimeChart,
            label = "Rest Time",
            chartColor = Color.YELLOW,
            xAxisLabels = xAxisLabels,
            yAxisUnit = "sec"
        )
    }

    private fun populateWeightsUsedCard() {
        val totalVolume = allSessions.sumOf {
            it.session.repsPerSet.zip(it.session.weightsPerSet)
                .sumOf { (reps, weight) -> (reps * weight).toDouble() }
        }.toLong()
        binding.totalWeightsLifted.text = String.format(Locale.US, "%,d kg", totalVolume)

        val volumeToday = allSessions.filter {
            val date = Instant.parse(it.session.startsAt).atZone(AppLocale.ZONE).toLocalDate()
            date == today
        }.sumOf {
            it.session.repsPerSet.zip(it.session.weightsPerSet)
                .sumOf { (reps, weight) -> (reps * weight).toDouble() }
        }.toLong()
        binding.weightsLiftedToday.text = String.format(Locale.US, "%,d kg", volumeToday)

        val allWeights = allSessions.flatMap { it.session.weightsPerSet }
        val averageWeight = if (allWeights.isNotEmpty()) allWeights.average() else 0.0
        binding.averageWeights.text = String.format(Locale.US, "%.1f kg", averageWeight)

        // Calculate comparison with last week
        val totalVolumeThisWeek = currentWeekSessions.sumOf {
            it.session.repsPerSet.zip(it.session.weightsPerSet)
                .sumOf { (reps, weight) -> (reps * weight).toDouble() }
        }.toLong()

        val totalVolumeLastWeek = lastWeekSessions.sumOf {
            it.session.repsPerSet.zip(it.session.weightsPerSet)
                .sumOf { (reps, weight) -> (reps * weight).toDouble() }
        }.toLong()

        val difference = totalVolumeThisWeek - totalVolumeLastWeek
        val percentageChange = if (totalVolumeLastWeek != 0L) {
            (difference.toDouble() / totalVolumeLastWeek) * 100
        } else {
            if (totalVolumeThisWeek > 0) 100.0 else 0.0 // Avoid division by zero
        }

        val description = when {
            percentageChange > 0 -> String.format("%.0f%% more", percentageChange)
            percentageChange < 0 -> String.format("%.0f%% less", -percentageChange)
            else -> "the same"
        }
        binding.weightsLiftedCompare.text = "You lifted $description volume compared to last week."

        // Prepare chart data
        val groupedSessions = groupSessionsByWeek(allSessions)
        val xAxisLabels = groupedSessions.mapIndexed { index, _ -> "Week ${index + 1}" }

        val dataEntries = groupedSessions.mapIndexed { index, weekGroup ->
            val weeklyVolume = weekGroup.sessions.sumOf {
                it.session.repsPerSet.zip(it.session.weightsPerSet)
                    .sumOf { (reps, weight) -> (reps * weight).toDouble() }
            }.toFloat()
            Entry(index.toFloat(), weeklyVolume)
        }

        setupLineChartData(
            dataEntries = dataEntries,
            chart = binding.weightsUsedChart,
            label = "Weight Lifted (Volume)",
            chartColor = Color.RED,
            xAxisLabels = xAxisLabels,
            yAxisUnit = "kg",
        )
    }


    private fun styleChart(
        chart: Chart<*>, xAxisLabels: List<String>? = null, yAxisUnit: String? = null
    ) {
        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        if (chart is BarLineChartBase<*>) {
            chart.axisRight.isEnabled = false
            chart.setScaleEnabled(false)

            chart.axisLeft.apply {
                textColor = neutralTextColor
                setDrawGridLines(true)
                gridColor = neutralGridColor
                axisLineColor = neutralTextColor
                if (yAxisUnit != null) {
                    valueFormatter = object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                            return "${value.toInt()} $yAxisUnit"
                        }
                    }
                }
            }

            chart.xAxis.apply {
                textColor = neutralTextColor
                setDrawGridLines(false)
                axisLineColor = neutralTextColor
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                if (xAxisLabels != null) {
                    valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                }
            }
        }
    }

    private fun stylePieChart(chart: PieChart) {
        chart.isDrawHoleEnabled = true
        chart.holeRadius = 58f
        chart.transparentCircleRadius = 61f
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false

        chart.legend.textColor = neutralTextColor
        chart.setEntryLabelColor(neutralTextColor)
    }

    private fun setupLineChartData(
        dataEntries: List<Entry> = listOf(),
        chart: LineChart,
        label: String,
        chartColor: Int,
        xAxisLabels: List<String>,
        yAxisUnit: String = "",
        configureChart: (LineChart.() -> Unit)? = null,
        configureDataset: (LineDataSet.() -> Unit)? = null,
    ) {
        if (dataEntries.isEmpty()) {
            chart.clear()
            chart.invalidate()
            return
        }

        val dataSet = LineDataSet(dataEntries, label).apply {
            color = chartColor
            valueTextColor = neutralTextColor
            setCircleColor(chartColor)
            lineWidth = 2.5f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
        chart.data = LineData(dataSet)
        styleChart(chart, xAxisLabels, yAxisUnit)
        configureChart?.invoke(chart)
        configureDataset?.invoke(dataSet)
        chart.invalidate()
    }

    private fun setupBarChartData(
        dataEntries: List<BarEntry> = listOf(),
        chart: BarChart,
        label: String,
        xAxisLabels: List<String>,
        configureChart: (BarChart.() -> Unit)? = null,
        configureDataset: (BarDataSet.() -> Unit)? = null
    ) {
        if (dataEntries.isEmpty()) {
            chart.clear()
            chart.invalidate()
            return
        }

        val dataSet = BarDataSet(dataEntries, label).apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = neutralTextColor
            valueTextSize = 10f
        }
        chart.data = BarData(dataSet)
        styleChart(chart, xAxisLabels)
        configureChart?.invoke(chart)
        configureDataset?.invoke(dataSet)
        chart.invalidate()
    }

    private fun setupPieChartData(
        dataEntries: List<PieEntry> = listOf(),
        chart: PieChart,
        label: String,
        configureChart: (PieChart.() -> Unit)? = null,
        configureDataset: (PieDataSet.() -> Unit)? = null
    ) {
        if (dataEntries.isEmpty()) {
            chart.clear()
            chart.invalidate()
            return
        }

        val dataSet = PieDataSet(dataEntries, label).apply {
            colors = ColorTemplate.VORDIPLOM_COLORS.toList()
            valueFormatter = PercentFormatter(chart)
            valueTextSize = 12f
            valueTextColor = neutralTextColor
            sliceSpace = 2f
        }

        chart.data = PieData(dataSet)
        stylePieChart(chart)
        configureChart?.invoke(chart)
        configureDataset?.invoke(dataSet)
        chart.invalidate()
    }

    /**
     * Sets up a scroll listener to animate charts ONLY when they are scrolled into view.
     */
    private fun setupScrollAnimations() {
        val scrollView = binding.scrollView
        val chartsToAnimate = listOf(
            binding.averageTimeChart,
            binding.setsWidgetChart,
            binding.hoursSpentChart,
            binding.restTimeChart,
            binding.weightsUsedChart
        )

        // Initial check for visible charts
        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Animate any charts already visible on screen
                animateVisibleCharts(scrollView, chartsToAnimate)
                // Remove the listener to prevent it from running multiple times
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        // Scroll listener for charts that become visible later
        scrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                animateVisibleCharts(scrollView, chartsToAnimate)
            })
    }

    private fun animateVisibleCharts(
        scrollView: NestedScrollView,
        chartsToAnimate: List<Chart<*>>
    ) {
        val scrollBounds = Rect()
        scrollView.getDrawingRect(scrollBounds)

        for (chart in chartsToAnimate) {
            val chartBounds = Rect()
            chart.getHitRect(chartBounds)

            // Animate only if the chart is visible and hasn't been animated yet
            if (Rect.intersects(scrollBounds, chartBounds) && !animatedViews.contains(chart)) {
                if (chart.data == null || chart.data.entryCount == 0) continue

                when (chart) {
                    is LineChart, is BarChart, is PieChart -> chart.animateY(
                        1000,
                        Easing.EaseInOutCubic
                    )
                }
                animatedViews.add(chart)
            }
        }
    }
}
