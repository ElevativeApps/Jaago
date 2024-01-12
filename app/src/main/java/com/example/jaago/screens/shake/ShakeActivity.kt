package com.example.jaago.screens.shake

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import com.example.jaago.MyApplication
import com.example.jaago.R
import com.example.jaago.SoundPlayerManager
import com.example.jaago.utils.ShakeDetector

class ShakeActivity : AppCompatActivity() , SensorEventListener{
    private lateinit var sensorManager: SensorManager
    private var remainingRepetitions: Int = 2
    private lateinit var soundPlayerManager: SoundPlayerManager
    private lateinit var tvShake: TextView
    private var isShakeDetected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)
        soundPlayerManager = (application as MyApplication).soundPlayerManager
        remainingRepetitions = intent.getIntExtra("REPETITIONS" , 2)
        Log.d("Shake Count" ," $remainingRepetitions")
        tvShake = findViewById(R.id.tv_shake)
        setUpSensor()
    }

    private fun setUpSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this ,
                it ,
                SensorManager.SENSOR_DELAY_GAME ,
                SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

//    override fun onShakeDetected() {
//        remainingRepetitions--
//        tvShake.text = remainingRepetitions.toString()
//        // Check if remaining repetitions are completed
//        if (remainingRepetitions <= 0) {
//            soundPlayerManager.stop()
//            finish() // Close the activity when all repetitions are completed
//        }
//    }


    override fun onSensorChanged(event: SensorEvent?) {
        if( event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            val sides = event.values[0]
            tvShake.apply {
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -10
            }
            if (remainingRepetitions > 0 && !isShakeDetected) {
                // Check for a shake
                if (sides > SHAKE_THRESHOLD) {
                    isShakeDetected = true
                    remainingRepetitions--
                    tvShake.text = "Shakes remaining: $remainingRepetitions"
                    if (remainingRepetitions == 0) {
                        // If no more shakes remaining, close the activity
                        soundPlayerManager.stop()
                        finish()
                    }
                }
            } else {
                // Reset the flag after a certain delay to allow for the next shake detection
                Handler(Looper.getMainLooper()).postDelayed({
                    isShakeDetected = false
                }, SHAKE_RESET_DELAY_MILLIS)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayerManager.stop()
        sensorManager.unregisterListener(this)
    }
    companion object {
        private const val SHAKE_THRESHOLD = 10.0
        private const val SHAKE_RESET_DELAY_MILLIS = 1000L
    }
}