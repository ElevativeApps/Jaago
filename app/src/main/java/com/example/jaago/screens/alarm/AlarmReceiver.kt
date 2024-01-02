package com.example.jaago.screens.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.jaago.SoundPlayer
import com.example.jaago.screens.stopAlarm.StopAlarm

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Handle the alarm event, show the notification, or start the activity
        val alarmId = intent?.getLongExtra("ALARM_ID", -1L) ?: -1L
        if (alarmId != -1L) {
            val stopIntent = Intent(context, StopAlarm::class.java).apply {
                putExtra("ALARM_ID", alarmId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context?.startActivity(stopIntent)
//            SoundPlayer(context!!).play()
        }
    }
}