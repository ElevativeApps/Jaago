package com.example.jaago.screens.alarm

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.R
import com.example.jaago.model.AlarmItem
import com.example.jaago.screens.alarm.viewholder.AlarmViewHolder

class AlarmAdapter(private val alarms: MutableList<AlarmItem>, private val onDeleteClickListener: (position: Int) -> Unit ,
                   private val onItemClick: (position: Int) -> Unit , context: Context
) : RecyclerView.Adapter<AlarmViewHolder>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view , onDeleteClickListener , onItemClick , this)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.bind(alarm)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
        holder.switchAlarm.isChecked = retrieveAlarmState(alarm)

        holder.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            // Update the state of the AlarmItem when the switch is toggled
            alarm.isChecked = isChecked
            saveAlarmState(alarm)

        }
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    fun addAlarm(alarm: AlarmItem) {
        alarms.add(alarm)
        saveAlarmState(alarm)
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }

    private fun saveAlarmState(alarm: AlarmItem) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(alarm.id.toString(), alarm.isChecked)
        editor.apply()
    }

    fun retrieveAlarmState(alarm: AlarmItem): Boolean {
        return sharedPreferences.getBoolean(alarm.id.toString(), alarm.isChecked)
    }
}