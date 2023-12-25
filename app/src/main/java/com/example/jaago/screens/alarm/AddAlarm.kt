package com.example.jaago.screens.alarm

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TimePicker
import com.example.jaago.R
import com.google.android.material.button.MaterialButton

class AddAlarm : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        val hourPicker = findViewById<NumberPicker>(R.id.hourPicker)
        val minutePicker = findViewById<NumberPicker>(R.id.minutePicker)
        val saveButton = findViewById<Button>(R.id.btn_set_alarm)

        // Set up NumberPicker for hours (Assuming 24-hour format)
        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        hourPicker.wrapSelectorWheel = true

        // Set up NumberPicker for minutes
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.wrapSelectorWheel = true

        val formatter : NumberPicker.Formatter = NumberPicker.Formatter { String.format("%02d",it) }
        hourPicker.setFormatter(formatter)
        minutePicker.setFormatter(formatter)

        saveButton.setOnClickListener {
            val hour = hourPicker.value
            val minute = minutePicker.value

            val selectedTime = "$hour:$minute"
            val resultIntent = Intent()
            resultIntent.putExtra(SELECTED_TIME, selectedTime)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        const val SELECTED_TIME = "selected_time"
    }
}