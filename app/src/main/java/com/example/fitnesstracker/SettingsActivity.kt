package com.example.fitnesstracker

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnesstracker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar with back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        loadSettings()

        binding.buttonSave.setOnClickListener {
            saveSettings()
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)

        // Load fitness goals
        binding.editStepsGoal.setText(prefs.getInt("steps_goal", 10000).toString())
        binding.editDistanceGoal.setText(prefs.getInt("distance_goal", 8000).toString())
        binding.editCaloriesGoal.setText(prefs.getInt("calories_goal", 500).toString())

        // Load user profile
        binding.editWeight.setText(prefs.getFloat("weight", 70f).toString())
        binding.editHeight.setText(prefs.getInt("height", 175).toString())
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Save fitness goals
        try {
            editor.putInt("steps_goal", binding.editStepsGoal.text.toString().toInt())
            editor.putInt("distance_goal", binding.editDistanceGoal.text.toString().toInt())
            editor.putInt("calories_goal", binding.editCaloriesGoal.text.toString().toInt())

            // Save user profile
            editor.putFloat("weight", binding.editWeight.text.toString().toFloat())
            editor.putInt("height", binding.editHeight.text.toString().toInt())
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        editor.apply()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}