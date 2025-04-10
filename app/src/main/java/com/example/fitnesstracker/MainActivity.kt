package com.example.fitnesstracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fitnesstracker.data.AppDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var fabStartTracking: FloatingActionButton
    private lateinit var progressSteps: ProgressBar
    private lateinit var progressDistance: ProgressBar
    private lateinit var progressCalories: ProgressBar
    private lateinit var textSteps: TextView
    private lateinit var textDistance: TextView
    private lateinit var textCalories: TextView
    private lateinit var hourlyChart: BarChart
    private lateinit var weeklyChart: LineChart

    private var isTracking = false
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize UI components
        fabStartTracking = findViewById(R.id.fabStartTracking)
        progressSteps = findViewById(R.id.progressSteps)
        progressDistance = findViewById(R.id.progressDistance)
        progressCalories = findViewById(R.id.progressCalories)
        textSteps = findViewById(R.id.textSteps)
        textDistance = findViewById(R.id.textDistance)
        textCalories = findViewById(R.id.textCalories)
        hourlyChart = findViewById(R.id.hourlyChart)
        weeklyChart = findViewById(R.id.weeklyChart)

        // Set up charts
        setupCharts()

        // Button click listener
        fabStartTracking.setOnClickListener {
            if (isTracking) {
                stopTracking()
            } else {
                if (checkPermissions()) {
                    startTracking()
                }
            }
        }

        // Update data
        updateStats()
        updateButtonIcon()
    }

    private fun setupCharts() {
        // Configure hourly chart
        hourlyChart.description.isEnabled = false
        hourlyChart.setNoDataText("No activity data for today")

        // Configure weekly chart
        weeklyChart.description.isEnabled = false
        weeklyChart.setNoDataText("No weekly data available")

        // You would add more chart configuration here
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION
                )
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking()
            }
        }
    }

    private fun startTracking() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        isTracking = true
        updateButtonIcon()
    }

    private fun stopTracking() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        stopService(serviceIntent)
        isTracking = false
        updateButtonIcon()
    }

    private fun updateButtonIcon() {
        fabStartTracking.setImageResource(
            if (isTracking) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )
    }

    private fun updateStats() {
        CoroutineScope(Dispatchers.IO).launch {
            val fitnessData = database.fitnessDao().getLatestFitnessData()

            withContext(Dispatchers.Main) {
                if (fitnessData != null) {
                    // Update progress bars
                    progressSteps.progress = fitnessData.steps
                    progressDistance.progress = fitnessData.distance.toInt()
                    progressCalories.progress = fitnessData.calories.toInt()

                    // Update text views
                    textSteps.text = "${fitnessData.steps} / 10,000"
                    textDistance.text = "${String.format("%.2f", fitnessData.distance)} m / 8,000 m"
                    textCalories.text = "${String.format("%.2f", fitnessData.calories)} / 500"

                    // Update charts
                    updateHourlyChart()
                    updateWeeklyChart()
                }
            }
        }
    }

    private fun updateHourlyChart() {
        // Implement hourly chart data update here
        // This would query your database for hourly data and update the chart
    }

    private fun updateWeeklyChart() {
        // Implement weekly chart data update here
        // This would query your database for weekly data and update the chart
    }

    override fun onResume() {
        super.onResume()
        updateStats()
    }

    companion object {
        private const val PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 100
    }
}