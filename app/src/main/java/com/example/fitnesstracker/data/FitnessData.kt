package com.example.fitnesstracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "fitness_data")
data class FitnessData(
    @PrimaryKey val id: Long = 0,
    val date: Long,
    val steps: Int,
    val distance: Float, // in meters
    val calories: Float,
    val hourOfDay: Int
)