package com.example.jaago.screens.typing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.jaago.MyApplication
import com.example.jaago.R
import com.example.jaago.SoundPlayerManager
import com.example.jaago.model.SentenceItem

class TypingActivity : AppCompatActivity() {
    private lateinit var tvSentence: TextView
    private lateinit var btnOk: Button
    private lateinit var etSentence: EditText
    private var selectedSentence: String ?= null
    private lateinit var soundPlayerManager: SoundPlayerManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_typing)
        btnOk = findViewById(R.id.btn_ok)
        etSentence = findViewById(R.id.et_text)
        tvSentence = findViewById(R.id.tv_sentence)
        soundPlayerManager = (application as MyApplication).soundPlayerManager
        selectedSentence = intent.getStringExtra("SELECTED_SENTENCE")
        Log.d("selected_sentence_act" , "$selectedSentence")
        tvSentence.text = selectedSentence
        btnOk.setOnClickListener{
            val inputSentence = etSentence.text.toString().trim()
            if (inputSentence.isNotEmpty()) {
                if( inputSentence == selectedSentence?.trim() ){
                    soundPlayerManager.stop()
                    finish()
                } else {
                    etSentence.error = "Incorrect Sentence"
                }

            } else {
                // Show an error message if the input is empty
                etSentence.error = "Enter a sentence"
            }
        }

    }
    override fun onDestroy() {
        soundPlayerManager.stop()
        super.onDestroy()
    }
}