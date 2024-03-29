package com.example.jaago.screens.alarm

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.R
import com.example.jaago.model.MathQuestion
import com.example.jaago.model.RingtoneModel
import com.example.jaago.screens.maths.MathsPuzzle
import com.example.jaago.screens.shake.ShakePuzzle
import com.example.jaago.screens.typing.TypingPuzzle
import java.io.IOException

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
    private lateinit var mathsPuzzle: ImageView
    private lateinit var shakePuzzle: ImageView
    private lateinit var typingPuzzle: ImageView
    private lateinit var ringtoneLayout: LinearLayout
    private var selectedDays: MutableList<String>? = null
    private var isSelectedEveryDay: Boolean = false
    private var isSelectedWeekDay: Boolean = false
    private var isSelectedWeekEnd: Boolean = false
    private var seekBarValue: String? = null
    private var selectedRingtoneUri: String? = null
    private var repetitions: Int? = null
    private var shakeRepetitions: Int? = null
    private lateinit var mathQuestions: Array<MathQuestion?>
    private var puzzle: String? = null
    private var selectedText: String? = null
    private var selectedRingtone: RingtoneModel? = null
    private lateinit var mediaPlayer: MediaPlayer
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
            val selectedId = intent.getLongExtra(SELECTED_ID, -1)
            resultIntent.putExtra(SELECTED_ID, selectedId)
            resultIntent.putExtra(SELECTED_TIME, selectedTime)
            resultIntent.putExtra(SELECTED_DAYS, selectedDays?.toTypedArray())
            if( selectedId != -1L ){
                var selectedRepetitions = intent.getIntExtra(SELECTED_REPETITIONS , 1)
                var selectedSeekBar = intent.getStringExtra(SELECTED_SEEK_BAR_VALUE)
                if( seekBarValue != null && repetitions != null ){
                    selectedRepetitions = repetitions as Int
                    selectedSeekBar = seekBarValue
                }
                resultIntent.putExtra(SEEK_BAR_VALUE, selectedSeekBar)
                resultIntent.putExtra(NUMBER_PICKER_VALUE, selectedRepetitions)
                resultIntent.putExtra(NUMBER_PICKER_VALUE_SHAKE , shakeRepetitions)
            } else {
                resultIntent.putExtra(SEEK_BAR_VALUE, seekBarValue)
                resultIntent.putExtra(NUMBER_PICKER_VALUE_SHAKE, repetitions)
                resultIntent.putExtra(NUMBER_PICKER_VALUE_SHAKE , shakeRepetitions)
            }
            resultIntent.putExtra(SELECTED_SENTENCE , selectedText)
            resultIntent.putExtra(PUZZLE , puzzle)
            resultIntent.putExtra(RINGTONE, selectedRingtoneUri)
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
        mathsPuzzle = findViewById(R.id.iv_maths)
        shakePuzzle = findViewById(R.id.iv_shake)
        typingPuzzle = findViewById(R.id.iv_typing)
        ringtoneLayout = findViewById(R.id.ll_ringtone)
        mediaPlayer = MediaPlayer()
        everyDay.setOnClickListener {
            markEveryDay()
        }
        weekDay.setOnClickListener {
            markWeekDay()
        }
        weekEnd.setOnClickListener {
            markWeekEnd()
        }
        ringtoneLayout.setOnClickListener {
            showRingtoneDialog()
        }

        val selectedId = intent.getLongExtra(SELECTED_ID, -1)
        val selectedRepetitions1 = intent.getIntExtra(SELECTED_REPETITIONS , 1)
        val selectedSeekBar1 = intent.getStringExtra(SELECTED_SEEK_BAR_VALUE)

        mathsPuzzle.setOnClickListener {
            val intent = Intent( this , MathsPuzzle::class.java)
            if( selectedId != -1L){
                intent.putExtra(SAVED_REPETITIONS, selectedRepetitions1)
                intent.putExtra(SAVED_SEEK_BAR, selectedSeekBar1)
            }
            startActivityForResult(intent, MATHS_PUZZLE_REQUEST_CODE)
        }

        shakePuzzle.setOnClickListener {
            val intent = Intent( this , ShakePuzzle::class.java)
            if( selectedId != -1L){
                intent.putExtra(SAVED_REPETITIONS, selectedRepetitions1)
            }
            startActivityForResult(intent, SHAKE_PUZZLE_REQUEST_CODE)
        }

        typingPuzzle.setOnClickListener {
            val intent = Intent( this , TypingPuzzle::class.java)
            startActivityForResult(intent, TYPING_PUZZLE_REQUEST_CODE)
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

    private fun showRingtoneDialog() {
        val ringtoneList: List<RingtoneModel> = getRingtoneList()

        val adapter = RingtoneAdapter(ringtoneList) { selectedRingtone ->
            // Handle the selected ringtone
            // You can play the ringtone here for preview if needed
            playRingtone(selectedRingtone.uri)
            this.selectedRingtone = selectedRingtone
            selectedRingtoneUri = selectedRingtone.uri.toString()
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_ringtone_selection, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvRingtoneList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Ringtone")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                // Handle the OK button click
                // Set the selected ringtone for the alarm

//                saveRingtoneWithAlarm(selectedRingtone)
                stopRingtone()
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Stop playing the ringtone if canceled
                stopRingtone()
            }
            .create()

        dialog.show()
    }
    private fun playRingtone(ringtoneUri: Uri) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this, ringtoneUri)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            Log.e("MediaPlayer", "Error playing ringtone", e)
        }
    }

    private fun stopRingtone() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }
    private fun getRingtoneList(): List<RingtoneModel> {
        val ringtoneList = mutableListOf<RingtoneModel>()

        // Add specific ringtones to the list
        ringtoneList.add(RingtoneModel("Alarm 1", Uri.parse("android.resource://${packageName}/${R.raw.alarm1}")))
        ringtoneList.add(RingtoneModel("Alarm 2", Uri.parse("android.resource://${packageName}/${R.raw.alarm2}")))
        ringtoneList.add(RingtoneModel("Alarm 3", Uri.parse("android.resource://${packageName}/${R.raw.alarm3}")))
        ringtoneList.add(RingtoneModel("Alarm 4", Uri.parse("android.resource://${packageName}/${R.raw.alarm4}")))
        ringtoneList.add(RingtoneModel("Alarm 5", Uri.parse("android.resource://${packageName}/${R.raw.alarm5}")))
        ringtoneList.add(RingtoneModel("Alarm 6", Uri.parse("android.resource://${packageName}/${R.raw.alarm6}")))
        ringtoneList.add(RingtoneModel("Alarm 7", Uri.parse("android.resource://${packageName}/${R.raw.test1}")))

        return ringtoneList
    }
    private fun saveRingtoneWithAlarm(ringtone: RingtoneModel?) {
        // Save the selected ringtone with the alarm
        // You can save 'ringtone.uri.toString()' or any other necessary information

        // Example: Save to SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedRingtoneUri", ringtone?.uri.toString())
        editor.apply()

        // Set the result to be sent back to the calling activity (AlarmActivity)
        val resultIntent = Intent()
        resultIntent.putExtra("selectedRingtoneUri", ringtone?.uri.toString())
        setResult(Activity.RESULT_OK, resultIntent)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MATHS_PUZZLE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Handle the data returned from MathsPuzzle activity
            seekBarValue = data?.getStringExtra(MathsPuzzle.EXTRA_SEEK_BAR_VALUE)
            repetitions = data?.getIntExtra(MathsPuzzle.EXTRA_NUMBER_PICKER_VALUE , 1 )
            puzzle = data?.getStringExtra(MathsPuzzle.EXTRA_PUZZLE)
        } else if( requestCode == SHAKE_PUZZLE_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {
            shakeRepetitions = data?.getIntExtra(ShakePuzzle.EXTRA_NUMBER_PICKER_VALUE_SHAKE , 2 )
            puzzle = data?.getStringExtra(ShakePuzzle.EXTRA_PUZZLE)
        } else if ( requestCode == TYPING_PUZZLE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedText = data?.getStringExtra(TypingPuzzle.EXTRA_SENTENCE)
            puzzle = data?.getStringExtra(TypingPuzzle.EXTRA_PUZZLE)
        }
//        else if (requestCode == RINGTONE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            // Handle the result from AddAlarm activity
//            selectedRingtoneUri = data?.getStringExtra("selectedRingtoneUri")
//
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources
        mediaPlayer.release()
    }
    companion object {
        const val SELECTED_ID = "selected_id"
        const val SELECTED_TIME = "selected_time"
        const val SELECTED_DAYS = "selected_days"
        const val SELECTED_SENTENCE = "selected_sentence"
        const val SEEK_BAR_VALUE = "seek_bar_value"
        const val NUMBER_PICKER_VALUE = "number_picker_value"
        const val NUMBER_PICKER_VALUE_SHAKE = "number_picker_value_shake"
        const val MATH_QUESTIONS = "math_question"
        const val PUZZLE = "puzzle"
        const val SELECTED_REPETITIONS = "selected_repetitions"
        const val SELECTED_SEEK_BAR_VALUE = "selected_seek_bar_value"
        const val MATHS_PUZZLE_REQUEST_CODE = 123
        const val SAVED_REPETITIONS = "saved_repetitions"
        const val SAVED_SEEK_BAR = "saved_seek_bar"
        const val SHAKE_PUZZLE_REQUEST_CODE = 456
        const val TYPING_PUZZLE_REQUEST_CODE = 789
        const val RINGTONE_REQUEST_CODE = 333
        const val RINGTONE = "ringtone"
    }
}