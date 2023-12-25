package com.example.jaago.screens.base

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jaago.R
import com.example.jaago.screens.alarm.AlarmActivity
import com.example.jaago.screens.stopwatch.StopwatchActivity
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
                R.id.action_stopwatch -> {
                    if (javaClass != StopwatchActivity::class.java) {
                        startActivity(Intent(this, StopwatchActivity::class.java))
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
            is StopwatchActivity -> bottomNavigationView.selectedItemId = R.id.action_stopwatch
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                // Handle option 1 click
                Toast.makeText(this , "Edit option Clicked" , Toast.LENGTH_SHORT).show()

                return true
            }
            R.id.settings -> {
                // Handle option 2 click
                Toast.makeText(this , "Settings option Clicked" , Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}