package com.example.jaago.screens.stopAlarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.jaago.MyApplication
import com.example.jaago.R
import com.example.jaago.SoundPlayer
import com.example.jaago.SoundPlayerManager

class StopAlarm : AppCompatActivity() {
    private lateinit var soundPlayerManager: SoundPlayerManager
    private lateinit var stopAlarmItem: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_alarm)

        soundPlayerManager = (application as MyApplication).soundPlayerManager
        stopAlarmItem = findViewById(R.id.btn_stop)
        val alarmId = intent.getLongExtra("ALARM_ID", -1L)

        if (alarmId != -1L) {
            soundPlayerManager.play(this)
        } else {
            finish()
        }

        stopAlarmItem.setOnClickListener {
            soundPlayerManager.stop()
            finish()
        }
    }

    override fun onDestroy() {
        soundPlayerManager.stop()
        super.onDestroy()
    }
}