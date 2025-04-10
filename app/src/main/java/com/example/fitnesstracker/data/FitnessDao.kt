package com.example.fitnesstracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface FitnessDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fitnessData: FitnessData)

    @Query("SELECT * FROM fitness_data WHERE date >= :startDate ORDER BY date DESC")
    fun getRecentData(startDate: Long): LiveData<List<FitnessData>>

    @Query("SELECT SUM(steps) FROM fitness_data WHERE date >= :startDate")
    fun getTotalSteps(startDate: Long): LiveData<Int>

    @Query("SELECT SUM(distance) FROM fitness_data WHERE date >= :startDate")
    fun getTotalDistance(startDate: Long): LiveData<Float>

    @Query("SELECT SUM(calories) FROM fitness_data WHERE date >= :startDate")
    fun getTotalCalories(startDate: Long): LiveData<Float>

    @Query("SELECT hourOfDay, SUM(steps) as totalSteps, SUM(distance) as totalDistance, SUM(calories) as totalCalories FROM fitness_data WHERE date >= :startDate GROUP BY hourOfDay ORDER BY hourOfDay")
    fun getHourlyData(startDate: Long): LiveData<List<HourlyFitnessData>>

    @Query("SELECT * FROM fitness_data ORDER BY date DESC LIMIT 1")
    suspend fun getLatestFitnessData(): FitnessData?
}



