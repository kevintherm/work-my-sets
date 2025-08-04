package com.example.workmysets.activities

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.workmysets.R
import com.example.workmysets.databinding.ActivityWidgetsBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class WidgetsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWidgetsBinding

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

        binding.topBar.titleText.text = "Stats"
        binding.topBar.backButton.visibility = View.VISIBLE
        binding.topBar.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.topBar.actionButton.visibility = View.VISIBLE
        binding.topBar.actionButton.isClickable = false
        binding.topBar.actionButton.isFocusable = false
        binding.topBar.actionButton.setImageDrawable(getDrawable(R.drawable.ic_bar_chart))

        populateAverageTimeCard()
        populateSetsCard()
        populateHoursSpentCard()
        populateRestTimeCard()
        populateWeightsUsedCard()
    }

    private fun populateAverageTimeCard() {
        binding.averageWorkoutTime.text = "10:35 AM"
        setupChart(binding.averageTimeChart, "Workout Time", Color.CYAN)
    }

    private fun populateSetsCard() {
        binding.totalSets.text = "125"
        binding.totalSetsToday.text = "15"
        binding.averageSets.text = "4"
        setupChart(binding.setsWidgetChart, "Total Sets", Color.GREEN)
    }

    private fun populateHoursSpentCard() {
        binding.totalHoursSpent.text = "5h 30m"
        binding.hoursSpentToday.text = "1h 15m"
        binding.averageDuration.text = "45m"
        setupChart(binding.hoursSpentChart, "Hours Spent", Color.MAGENTA)
    }

    private fun populateRestTimeCard() {
        binding.totalRestTime.text = "1h 10m"
        binding.restTimeToday.text = "20m"
        binding.averageRestTime.text = "1m 30s"
        setupChart(binding.restTimeChart, "Rest Time", Color.YELLOW)
    }

    private fun populateWeightsUsedCard() {
        binding.totalWeightsLifted.text = "5,400 kg"
        binding.weightsLiftedToday.text = "750 kg"
        binding.averageWeights.text = "60 kg"
        setupChart(binding.weightsUsedChart, "Weight Lifted (kg)", Color.RED)
    }

    private fun setupChart(chart: LineChart, label: String, chartColor: Int) {
        val entries = arrayListOf(
            Entry(0f, 20f),
            Entry(1f, 24f),
            Entry(2f, 22f),
            Entry(3f, 28f),
            Entry(4f, 26f),
            Entry(5f, 30f),
            Entry(6f, 28f)
        )

        val dataSet = LineDataSet(entries, label).apply {
            color = chartColor
            valueTextColor = Color.BLACK
            setCircleColor(chartColor)
            setDrawCircleHole(false)
            lineWidth = 2.5f
            circleRadius = 4f
            valueTextSize = 10f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        chart.data = LineData(dataSet)

        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.textColor = Color.DKGRAY
            axisLeft.setDrawGridLines(false)
            axisLeft.textColor = Color.DKGRAY
            invalidate()
        }

        // Defer until layout is complete
        chart.viewTreeObserver.addOnGlobalLayoutListener {
            if (isViewVisible(chart)) {
                // Already visible, skip animation
                return@addOnGlobalLayoutListener
            }

            var animated = false
            binding.main.viewTreeObserver.addOnScrollChangedListener {
                if (!animated && isViewVisible(chart)) {
                    chart.animateY(750, Easing.EaseInOutQuad)
                    animated = true
                }
            }
        }
    }

    private fun isViewVisible(view: View): Boolean {
        val scrollBounds = Rect()
        binding.main.getHitRect(scrollBounds)
        return view.getLocalVisibleRect(scrollBounds)
    }

}