package com.example.jaago.screens.shake

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import com.example.jaago.R
import com.example.jaago.screens.maths.MathsPuzzle
import com.example.jaago.screens.maths.MathsPuzzle.Companion.EXTRA_NUMBER_PICKER_VALUE
import java.nio.file.Files.find

class ShakePuzzle : AppCompatActivity() {

    private lateinit var numberPicker : NumberPicker
    private lateinit var btnOk : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake_puzzle)
        init()

    }

    private fun init(){
        numberPicker = findViewById(R.id.np_repeat_shake)
        numberPicker.minValue = 1
        numberPicker.maxValue = 30
        numberPicker.wrapSelectorWheel = false

        btnOk = findViewById(R.id.btn_ok)
        btnOk.setOnClickListener{
            val selectedRepeatValue = numberPicker.value
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_NUMBER_PICKER_VALUE_SHAKE, selectedRepeatValue)
            resultIntent.putExtra(EXTRA_PUZZLE, "SHAKE_PUZZLE")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_NUMBER_PICKER_VALUE_SHAKE = "extra_number_picker_value_shake"
        const val EXTRA_PUZZLE = "extra_shake_puzzle"
    }

}