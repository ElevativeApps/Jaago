package com.example.jaago.screens.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.R
import com.example.jaago.model.AlarmItem
import com.example.jaago.screens.alarm.viewholder.AlarmViewHolder

class AlarmAdapter(private val alarms: MutableList<AlarmItem>, private val onDeleteClickListener: (position: Int) -> Unit) : RecyclerView.Adapter<AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view , onDeleteClickListener)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.bind(alarm)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    fun addAlarm(alarm: AlarmItem) {
        alarms.add(alarm)
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }
}