package com.example.jaago.screens.alarm.viewholder

import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.R
import com.example.jaago.model.AlarmItem

class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val timeTextView: TextView = itemView.findViewById(R.id.tvTime)
    private val switchAlarm: Switch = itemView.findViewById(R.id.switchAlarm)

    fun bind(alarm: AlarmItem) {
        timeTextView.text = alarm.formattedTime
        switchAlarm.isChecked = alarm.isChecked
    }
}