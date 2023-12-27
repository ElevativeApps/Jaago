package com.example.jaago.screens.alarm

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.TimePicker
import com.example.jaago.R
import com.google.android.material.button.MaterialButton

class AddAlarm : AppCompatActivity() {
    private lateinit var everyDay: TextView
    private lateinit var weekDay: TextView
    private lateinit var weekEnd: TextView
    private lateinit var cbMonday: CheckBox
    private lateinit var cbTuesday: CheckBox
    private lateinit var cbWednesday: CheckBox
    private lateinit var cbThursday: CheckBox
    private lateinit var cbFriday: CheckBox
    private lateinit var cbSaturday: CheckBox
    private lateinit var cbSunday: CheckBox
    private var selectedDays: MutableList<String>? = null
    private var isSelectedEveryDay: Boolean = false
    private var isSelectedWeekDay: Boolean = false
    private var isSelectedWeekEnd: Boolean = false
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
            checkSelectedDays()
            val selectedTime = "$hour:$minute"
            val resultIntent = Intent()
            val uniqueId = System.currentTimeMillis()
            resultIntent.putExtra(SELECTED_ID, uniqueId)
            resultIntent.putExtra(SELECTED_TIME, selectedTime)
            resultIntent.putExtra(SELECTED_DAYS, selectedDays?.toTypedArray())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        init()

        val selectedTime = intent.getStringExtra(SELECTED_TIME)
        val selectedDays_1 = intent.getStringArrayExtra(SELECTED_DAYS)
        if (!selectedTime.isNullOrEmpty()) {
            val timeParts = selectedTime.split(":")
            hourPicker.value = timeParts[0].toInt()
            minutePicker.value = timeParts[1].toInt()
        }
        Log.d("selectedDays_1", "Selected Days: ${selectedDays_1?.contentToString()}")
        selectedDays_1?.forEach { day ->
            when (day) {
                "_monday" -> cbMonday.isChecked = true
                "_tuesday" -> cbTuesday.isChecked = true
                "_wednesday" -> cbWednesday.isChecked = true
                "_thursday" -> cbThursday.isChecked = true
                "_friday" -> cbFriday.isChecked = true
                "_saturday" -> cbSaturday.isChecked = true
                "_sunday" -> cbSunday.isChecked = true
            }
        }
    }

    private fun checkSelectedDays() {
        selectedDays = mutableListOf()
        val checkBoxes = arrayOf(cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday)
        for (checkBox in checkBoxes) {
            if (checkBox.isChecked) {
                // Extract the day from the CheckBox's ID
                val day = resources.getResourceEntryName(checkBox.id).substring(2)
                selectedDays?.add(day)
            }
        }
    }

    private fun init(){
        everyDay = findViewById(R.id.tv_everyDay)
        weekDay = findViewById(R.id.tv_weekDay)
        weekEnd = findViewById(R.id.tv_weekEnd)
        cbMonday = findViewById(R.id.cb_monday)
        cbTuesday = findViewById(R.id.cb_tuesday)
        cbWednesday = findViewById(R.id.cb_wednesday)
        cbThursday = findViewById(R.id.cb_thursday)
        cbFriday = findViewById(R.id.cb_friday)
        cbSaturday = findViewById(R.id.cb_saturday)
        cbSunday = findViewById(R.id.cb_sunday)

        everyDay.setOnClickListener {
            markEveryDay()
        }
        weekDay.setOnClickListener {
            markWeekDay()
        }
        weekEnd.setOnClickListener {
            markWeekEnd()
        }
    }

    private fun markWeekEnd() {
        if( isSelectedWeekEnd ){
            cbSaturday.isChecked = false
            cbSunday.isChecked = false
            isSelectedWeekEnd = false
        } else {
            cbSaturday.isChecked = true
            cbSunday.isChecked = true
            isSelectedWeekEnd = true
        }

    }

    private fun markWeekDay() {
        if( isSelectedWeekDay){
            cbMonday.isChecked = false
            cbTuesday.isChecked = false
            cbWednesday.isChecked = false
            cbThursday.isChecked = false
            cbFriday.isChecked = false
            isSelectedWeekDay = false
        } else {
            cbMonday.isChecked = true
            cbTuesday.isChecked = true
            cbWednesday.isChecked = true
            cbThursday.isChecked = true
            cbFriday.isChecked = true
            isSelectedWeekDay = true
        }

    }

    private fun markEveryDay() {
        if( isSelectedEveryDay ){
            cbMonday.isChecked = false
            cbTuesday.isChecked = false
            cbWednesday.isChecked = false
            cbThursday.isChecked = false
            cbFriday.isChecked = false
            cbSaturday.isChecked = false
            cbSunday.isChecked = false
            isSelectedEveryDay = false
        } else {
            cbMonday.isChecked = true
            cbTuesday.isChecked = true
            cbWednesday.isChecked = true
            cbThursday.isChecked = true
            cbFriday.isChecked = true
            cbSaturday.isChecked = true
            cbSunday.isChecked = true
            isSelectedEveryDay = true
        }
    }

    companion object {
        const val SELECTED_ID = "selected_id"
        const val SELECTED_TIME = "selected_time"
        const val SELECTED_DAYS = "selected_days"
    }
}