package com.example.jaago.screens.typing

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.R
import com.example.jaago.model.SentenceItem
import com.example.jaago.screens.shake.ShakePuzzle

class TypingPuzzle : AppCompatActivity() {
    private val sentencesList = mutableListOf<SentenceItem>()
    private var defaultSentencesList = mutableListOf<SentenceItem>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SentenceAdapter
    private lateinit var btnOk : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_typing_puzzle)
        val addButton: ImageView = findViewById(R.id.iv_add)
        recyclerView = findViewById(R.id.rv_sentences)
        btnOk = findViewById(R.id.btn_ok)
        adapter = SentenceAdapter(sentencesList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        defaultSentencesList = mutableListOf(
            SentenceItem("Hare Krishna, Hare Krishna, Krishna Krishna, Hare Hare Hare Rama, Hare Rama, Rama Rama, Hare Hare") ,
            SentenceItem("Ram Ram Ram Ram Ram Ram Ram Ram Ram Ram Ram") ,
            SentenceItem("Om Namah Shivay")
        )

        addButton.setOnClickListener {
            showAddSentenceDialog()
        }

        btnOk.setOnClickListener {
            val selectedText = getSelectedSentence()
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_SENTENCE, selectedText)
            resultIntent.putExtra(EXTRA_PUZZLE, "TYPING_PUZZLE")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
    private fun showAddSentenceDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_sentence)

        val etSentence: EditText = dialog.findViewById(R.id.etSentence)
        val btnAdd: Button = dialog.findViewById(R.id.btnAdd)

        btnAdd.setOnClickListener {
            val sentenceText = etSentence.text.toString().trim()

            if (sentenceText.isNotEmpty()) {
                val sentence = SentenceItem(sentenceText)
                sentencesList.add(sentence)
                // Dismiss the dialog
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            } else {
                // Show an error message if the input is empty
                etSentence.error = "Enter a sentence"
            }
        }

        // Show the dialog
        dialog.show()
    }
    private fun getSelectedSentence(): String? {
        for (sentence in sentencesList) {
            if (sentence.isSelected) {
                return sentence.text
            }
        }
        return null
    }
    override fun onResume() {
        super.onResume()
        sentencesList.clear()
        sentencesList.addAll(defaultSentencesList)
        if (sentencesList.isNotEmpty()) {
            sentencesList[0].isSelected = true
        }
        adapter.notifyDataSetChanged()
    }
    companion object {
        const val EXTRA_SENTENCE = "extra_sentence"
        const val EXTRA_PUZZLE = "extra_typing_puzzle"
    }
}