package com.example.jaago.screens.maths

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.SeekBar
import android.widget.TextView
import com.example.jaago.MyApplication
import com.example.jaago.R
import com.example.jaago.SoundPlayerManager
import com.example.jaago.model.MathQuestion
import com.example.jaago.model.generateMathQuestion
import com.example.jaago.screens.alarm.AddAlarm

class MathsPuzzle : AppCompatActivity() {
    private lateinit var btnOk : Button
    private lateinit var btnContinue : Button
    private lateinit var editTextNumber : EditText
    private lateinit var numberPicker : NumberPicker
    private lateinit var seekBar: SeekBar
    private lateinit var exampleMath : LinearLayout
    private lateinit var difficultyBar : LinearLayout
    private lateinit var repeatLayout : LinearLayout
    private lateinit var questionMath : LinearLayout
    private lateinit var val1 : TextView
    private lateinit var val2 : TextView
    private lateinit var val3 : TextView
    private lateinit var operator : TextView
    private lateinit var remaining: TextView
    private lateinit var mathQuestions: List<MathQuestion>
    private var currentQuestionIndex: Int = 0
    private var selectedRepetitions: Int = 1
    private var selectedSeekBarValue: String? = null
//    private lateinit var soundPlayerManager: SoundPlayerManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maths_puzzle)
//        soundPlayerManager = (application as MyApplication).soundPlayerManager
//        mathQuestions = intent.getParcelableArrayListExtra("MATH_QUESTIONS") ?: emptyList()
//        showCurrentQuestion()
        selectedRepetitions = intent.getIntExtra(AddAlarm.SELECTED_REPETITIONS, 1)
        selectedSeekBarValue = intent.getStringExtra(AddAlarm.SELECTED_SEEK_BAR_VALUE)
        Log.d("seek Bar" , "{$selectedSeekBarValue}")
        Log.d("repetitions " , "{$selectedRepetitions}")
        init()
    }
    private fun init(){
        btnOk = findViewById(R.id.btn_ok)
        numberPicker = findViewById(R.id.np_repeat)
        seekBar = findViewById(R.id.seekBar)
        exampleMath = findViewById(R.id.ll_example_math)
        difficultyBar = findViewById(R.id.ll_difficulty)
        repeatLayout = findViewById(R.id.ll_repeat)

        exampleMath.visibility = View.VISIBLE
        difficultyBar.visibility = View.VISIBLE
        repeatLayout.visibility = View.VISIBLE
        btnOk.visibility = View.VISIBLE

        seekBar.progress = getSeekBarProgress(selectedSeekBarValue)

        numberPicker.minValue = 1
        numberPicker.maxValue = 10
        numberPicker.wrapSelectorWheel = false
        numberPicker.value = selectedRepetitions

        btnOk.setOnClickListener{
            val selectedRepeatValue = numberPicker.value
            val selectedDifficulty = getDifficultyFromSeekBar()

//            println("Selected Repeat Value: $selectedRepeatValue")
//            println("Selected Difficulty: $selectedDifficulty")
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_SEEK_BAR_VALUE, selectedDifficulty)
            resultIntent.putExtra(EXTRA_NUMBER_PICKER_VALUE , selectedRepeatValue)
            resultIntent.putExtra(EXTRA_PUZZLE , "MATHS_PUZZLE")
            val mathQuestions = generateMathQuestions(selectedDifficulty, selectedRepeatValue)
            resultIntent.putExtra(EXTRA_MATH_QUESTIONS, mathQuestions.toTypedArray())

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }


    private fun getSeekBarProgress(value: String?): Int {
        return when (value) {
            "Easy" -> 0
            "Medium" -> 1
            "Hard" -> 2
            else -> 0
        }
    }
    private fun getDifficultyFromSeekBar(): String {
        return when (seekBar.progress) {
            0 -> "Easy"
            1 -> "Medium"
            2 -> "Hard"
            else -> "Unknown"
        }
    }
    private fun generateMathQuestions(difficulty: String, repeatValue: Int): List<MathQuestion> {
        val mathQuestions = mutableListOf<MathQuestion>()
        repeat(repeatValue) {
            val question = generateMathQuestion(difficulty)
            mathQuestions.add(question)
        }
        return mathQuestions
    }


    companion object {
        const val EXTRA_SEEK_BAR_VALUE = "extra_seek_bar_value"
        const val EXTRA_NUMBER_PICKER_VALUE = "extra_number_picker_value"
        const val EXTRA_MATH_QUESTIONS = "extra_math_questions"
        const val EXTRA_PUZZLE = "extra_maths_puzzle"
    }
}