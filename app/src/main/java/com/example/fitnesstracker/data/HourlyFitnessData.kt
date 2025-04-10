package com.example.fitnesstracker.data

data class HourlyFitnessData(
    val hourOfDay: Int,
    val totalSteps: Int,
    val totalDistance: Float,
    val totalCalories: Float
)
