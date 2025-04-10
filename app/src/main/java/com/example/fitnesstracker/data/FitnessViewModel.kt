package com.example.fitnesstracker

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.AppDatabase
import com.example.fitnesstracker.data.FitnessData
import com.example.fitnesstracker.data.HourlyFitnessData
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val fitnessDao = database.fitnessDao()

    private val _today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val todaySteps: LiveData<Int> = fitnessDao.getTotalSteps(_today.time.time)
    val todayDistance: LiveData<Float> = fitnessDao.getTotalDistance(_today.time.time)
    val todayCalories: LiveData<Float> = fitnessDao.getTotalCalories(_today.time.time)

    val weeklyData: LiveData<List<FitnessData>> = run {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        fitnessDao.getRecentData(calendar.time.time)
    }

    // Use the correct return type to match the DAO
    val hourlyData: LiveData<List<HourlyFitnessData>> = fitnessDao.getHourlyData(_today.time.time)

    // Track if we're currently tracking fitness activity
    private val _isTracking = MutableLiveData<Boolean>(false)
    val isTracking: LiveData<Boolean> = _isTracking

    fun startTracking() {
        _isTracking.value = true
        // Start collecting fitness data
        viewModelScope.launch {
            // Begin logging fitness data
            // This is where you would start your sensor listeners or service
            logFitnessActivity()
        }
    }

    fun stopTracking() {
        _isTracking.value = false
        // Stop collecting fitness data
        // This is where you would stop your sensor listeners or service
    }

    private fun logFitnessActivity() {
        // Implementation for collecting fitness data
        // For example, using step sensors, activity recognition, etc.
    }

    fun getCurrentGoals(): Triple<Int, Float, Float> {
        val prefs = getApplication<Application>().getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
        val stepsGoal = prefs.getInt("steps_goal", 10000)
        val distanceGoal = prefs.getInt("distance_goal", 8000).toFloat()
        val caloriesGoal = prefs.getInt("calories_goal", 500).toFloat()
        return Triple(stepsGoal, distanceGoal, caloriesGoal)
    }
}