package com.example.fitnesstracker

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.fitnesstracker.data.AppDatabase
import com.example.fitnesstracker.data.FitnessData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import android.content.pm.ServiceInfo
import android.util.Log

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounter: Sensor? = null
    private var initialSteps: Int = -1  // Fixed property
    private var currentSteps = 0
    private var lastSaveTime = 0L
    private lateinit var database: AppDatabase
    private val TAG = "StepCounterService"

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        createNotificationChannel()
        val notification = createNotification("Starting fitness tracking...")

        // CRITICAL FIX: Actually start as foreground service with the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        Log.d(TAG, "Service created and started in foreground")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stepCounter?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Step counter sensor registered")
        } ?: Log.e(TAG, "Step counter sensor not available on this device")

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val stepValue = event.values[0].toInt()
            Log.d(TAG, "Step sensor event received: $stepValue")

            if (initialSteps == -1) {
                initialSteps = stepValue
                currentSteps = 0
                Log.d(TAG, "Initial step count set to: $initialSteps")
                // Immediately update data and notification when service starts
                updateFitnessData()
                updateNotification()
            } else {
                // Calculate new step count
                val newStepCount = stepValue - initialSteps

                // Only update if steps have changed
                if (newStepCount != currentSteps) {
                    currentSteps = newStepCount
                    Log.d(TAG, "Current steps: $currentSteps")

                    // Update with minimal delay (2-3 seconds) to avoid excessive database writes
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastSaveTime > 2000) { // 2 seconds delay
                        lastSaveTime = currentTime
                        updateFitnessData()
                        updateNotification()
                        Log.d(TAG, "Updated fitness data and notification")
                    }
                }
            }
        }
    }

    private fun updateFitnessData() {
        val calendar = Calendar.getInstance()
        val distance = calculateDistance(currentSteps.toFloat())
        val calories = calculateCalories(currentSteps.toFloat(), distance)

        CoroutineScope(Dispatchers.IO).launch {
            database.fitnessDao().insert(
                FitnessData(
                    date = calendar.time.time,  // Convert Date to Long timestamp
                    steps = currentSteps,
                    distance = distance,
                    calories = calories,
                    hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                )
            )
            Log.d(TAG, "Saved to database: steps=$currentSteps, distance=$distance, calories=$calories")
        }
    }

    private fun calculateDistance(steps: Float): Float {
        // Average step length is about 0.762 meters (2.5 feet)
        return steps * 0.762f
    }

    private fun calculateCalories(steps: Float, distance: Float): Float {
        // Very basic calculation (adjust for more accuracy)
        // Approx 0.04 calories per step for an average person
        return steps * 0.04f
    }

    private fun updateNotification() {
        val notification = createNotification("Steps: $currentSteps")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Fitness Tracker Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows fitness tracking progress"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    private fun createNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Fitness Tracker")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for step counter
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Service destroyed, sensor listener unregistered")
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "fitness_tracker_channel"
    }
}