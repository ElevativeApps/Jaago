package com.example.jaago.screens.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jaago.R
import com.example.jaago.screens.alarm.AlarmActivity
import com.example.jaago.screens.settings.SettingsActivity
import com.example.jaago.screens.timer.TimerActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {
    private var menuInflated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_alarm -> {
                    if (javaClass != AlarmActivity::class.java) {
                        startActivity(Intent(this, AlarmActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }
                R.id.action_settings -> {
                    if (javaClass != SettingsActivity::class.java) {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }
                R.id.action_timer -> {
                    if (javaClass != TimerActivity::class.java) {
                        startActivity(Intent(this, TimerActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }
                else -> false
            }

        }

        when (this) {
            is SettingsActivity -> bottomNavigationView.selectedItemId = R.id.action_settings
            is TimerActivity -> bottomNavigationView.selectedItemId = R.id.action_timer
            else -> {
                if (javaClass != AlarmActivity::class.java) {
                    startActivity(Intent(this, AlarmActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
        }

    }

}