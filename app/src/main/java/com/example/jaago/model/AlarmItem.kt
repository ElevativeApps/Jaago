package com.example.jaago.model

import java.text.SimpleDateFormat
import java.util.*

data class AlarmItem(val time: String, val isChecked: Boolean){
    val formattedTime: String
        get() {
            val parsedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(time)
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(parsedTime!!)
        }
}