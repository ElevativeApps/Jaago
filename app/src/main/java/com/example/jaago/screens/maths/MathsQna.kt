package com.example.jaago.screens.maths

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.jaago.MyApplication
import com.example.jaago.R
import com.example.jaago.SoundPlayerManager
import com.example.jaago.model.MathQuestion
import com.example.jaago.model.generateMathQuestion

class MathsQna : AppCompatActivity() {

    private lateinit var btnContinue : Button
    private lateinit var editTextNumber : EditText
    private lateinit var remaining: TextView
    private lateinit var questionMath : LinearLayout
    private lateinit var mathQuestions: List<MathQuestion>
    private lateinit var seekBarValue: String
    private var repetitions: Int = 1
    private var currentQuestionIndex: Int = 0
    private lateinit var soundPlayerManager: SoundPlayerManager
    private lateinit var val1 : TextView
    private lateinit var val2 : TextView
    private lateinit var val3 : TextView
    private lateinit var operator : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maths_qna)

        btnContinue = findViewById(R.id.btn_continue)
        editTextNumber = findViewById(R.id.et_answer)
        remaining = findViewById(R.id.tv_remaining)
        questionMath = findViewById(R.id.ll_question_math)
        val1 = findViewById(R.id.tv_qval_1)
        val2 = findViewById(R.id.tv_qval_2)
        val3 = findViewById(R.id.tv_qval_3)
        operator = findViewById(R.id.tv_qoperator)


        soundPlayerManager = (application as MyApplication).soundPlayerManager
        seekBarValue = intent.getStringExtra("SEEK_BAR_VALUE").toString()
        repetitions = intent.getIntExtra("REPETITIONS" , 1)
        Log.d("seekBarValue_qna" , "$seekBarValue")
        Log.d("repetitions_qna" , "$repetitions")
        mathQuestions = generateMathQuestions(seekBarValue, repetitions)

        editTextNumber.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        showCurrentQuestion()
        if (currentQuestionIndex < mathQuestions.size) {

            btnContinue.setOnClickListener {
                handleContinueButtonClick()
            }
        } else {
            // show Good Morning Screen ( StopActivity )
        }
    }

    private fun showCurrentQuestion() {
        val question = mathQuestions[currentQuestionIndex]
        val1.text = question.operand1.toString()
        val2.text = question.operand2.toString()
        operator.text = question.operator
        remaining.text = "${currentQuestionIndex + 1}/${mathQuestions.size}"
    }

    private fun handleContinueButtonClick() {
        val userAnswer = editTextNumber.text.toString().toIntOrNull()
        if (userAnswer != null && userAnswer == mathQuestions[currentQuestionIndex].answer) {
            currentQuestionIndex++
            editTextNumber.text.clear()
            editTextNumber.setBackgroundResource(R.drawable.et_normal_bg)
            if (currentQuestionIndex < mathQuestions.size) {
                showCurrentQuestion()
            } else {
                soundPlayerManager.stop()
                finish()
            }
        } else {
            editTextNumber.setBackgroundResource(R.drawable.et_wrong_bg)
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
    override fun onDestroy() {
        soundPlayerManager.stop()
        super.onDestroy()
    }
}