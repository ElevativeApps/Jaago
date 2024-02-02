package com.example.jaago.screens.typing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.R
import com.example.jaago.model.SentenceItem

class SentenceAdapter(private val sentences: List<SentenceItem>) :
    RecyclerView.Adapter<SentenceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sentence, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sentence = sentences[position]
        val checkbox: CheckBox = holder.itemView.findViewById(R.id.checkbox)
        val textView: TextView = holder.itemView.findViewById(R.id.textview)

        // Set the text for the sentence
        textView.text = sentence.text
        checkbox.isChecked = sentence.isSelected

        checkbox.setOnClickListener {
            // Update the isSelected property
            sentence.isSelected = checkbox.isChecked

            // If the checkbox is checked, uncheck other checkboxes
            if (checkbox.isChecked) {
                for (i in sentences.indices) {
                    if (i != position) {
                        sentences[i].isSelected = false
                    }
                }
                notifyDataSetChanged() // Notify the adapter to update the UI
            }
        }
    }

    override fun getItemCount(): Int {
        return sentences.size
    }
}