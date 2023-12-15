package com.example.jaago.screens.timer

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action.equals("STOP_ACTION")) {
            // Stop the sound and cancel the notification

            Log.d("NotificationReceiver", "Received STOP_ACTION")
            val timerActivity = TimerActivity.instance
            timerActivity?.stopNotificationSound()
            timerActivity?.cancelNotification()
        }
    }
}