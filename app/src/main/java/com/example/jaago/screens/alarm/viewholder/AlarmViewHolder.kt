package com.example.jaago.screens.alarm.viewholder

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.R
import com.example.jaago.model.AlarmItem
import com.example.jaago.screens.alarm.AlarmAdapter

class AlarmViewHolder(itemView: View, private val onDeleteClickListener: (position: Int) -> Unit , private val onItemClick: (position: Int) -> Unit ,  private val alarmAdapter: AlarmAdapter) : RecyclerView.ViewHolder(itemView) {
    private val timeTextView: TextView = itemView.findViewById(R.id.tvTime)
    val switchAlarm: Switch = itemView.findViewById(R.id.switchAlarm)
    private val deleteIcon: ImageView = itemView.findViewById(R.id.iv_delete)

    init {
        deleteIcon.setOnClickListener {
            showDeleteDialog()
        }
        itemView.setOnClickListener {
            onItemClick(adapterPosition)
        }
    }

    private fun showDeleteDialog() {
        val context = itemView.context
        val builder = AlertDialog.Builder(context , R.style.AlertDialogCustomStyle )

        val inflater = LayoutInflater.from(itemView.context)
        val dialogView = inflater.inflate(R.layout.dialog_alarm_delete, null)

        builder.setView(dialogView)
            .setPositiveButton("Yes") { _, _ ->
                onDeleteClickListener(adapterPosition)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun bind(alarm: AlarmItem) {
        timeTextView.text = alarm.formattedTime
        switchAlarm.isChecked = alarmAdapter.retrieveAlarmState(alarm)
    }
}