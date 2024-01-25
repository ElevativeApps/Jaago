package com.example.jaago.model

import java.text.SimpleDateFormat
import java.util.*

data class AlarmItem(val id: Long, var time: String, var selectedDays: List<String>, var isChecked: Boolean ,
                     var puzzleType: String? , var seekBarDifficulty: String?, var repetitions: Int? , var shakeRepetitions: Int? , var selectedSentence: String? ){
    val formattedTime: String
        get() {
            val parsedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(time)
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(parsedTime!!)
        }
}