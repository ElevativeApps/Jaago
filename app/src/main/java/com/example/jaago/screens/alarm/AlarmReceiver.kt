package com.example.jaago.screens.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.jaago.MyApplication
import com.example.jaago.R
import com.example.jaago.screens.stopAlarm.StopAlarm
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Handle the alarm event, show the notification, or start the activity
        val alarmId = intent?.getLongExtra("ALARM_ID", -1L) ?: -1L
        val selectedDays = intent?.getStringArrayExtra("SELECTED_DAYS")
        Log.d("days_test_1" , "${selectedDays?.contentToString()}")
        if (alarmId != -1L) {
            // Show notification
            showNotification(context!!, alarmId, selectedDays)

            // Start StopAlarm activity
//            val stopIntent = Intent(context, StopAlarm::class.java).apply {
//                putExtra("ALARM_ID", alarmId)
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            }
//            context.startActivity(stopIntent)
        }
    }

    private fun showNotification(context: Context, alarmId: Long, selectedDays: Array<String>?) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent to launch the StopAlarm activity
        val stopIntent = Intent(context, StopAlarm::class.java).apply {
            putExtra("ALARM_ID", alarmId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create a notification
        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("Alarm")
            .setContentText("Your alarm is ringing!")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true) // Dismiss the notification when the user taps on it
            .setContentIntent(pendingIntent) // Set the intent to open StopAlarm activity

        // Show the notification
//        notificationManager.notify(alarmId.toInt(), notificationBuilder.build())

        // Check if there are selected days
        if (selectedDays != null && selectedDays.isNotEmpty()) {
            // Get the current day of the week
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val currentDayString = getDayOfWeekString(currentDay)

            // Check if the current day is one of the selected days
            if (selectedDays.contains(currentDayString)) {
                // Show the notification only on selected days
                // You can add additional actions or customize the notification as needed
                val soundPlayerManager = (context.applicationContext as MyApplication).soundPlayerManager
                soundPlayerManager.play(context)
                notificationManager.notify(alarmId.toInt(), notificationBuilder.build())
            }
        } else {
            // Show the notification immediately if there are no selected days
            // You can add additional actions or customize the notification as needed
            val soundPlayerManager = (context.applicationContext as MyApplication).soundPlayerManager
            soundPlayerManager.play(context)
            notificationManager.notify(alarmId.toInt(), notificationBuilder.build())
        }
    }

    private fun getDayOfWeekString(dayOfWeek: Int): String {
        // Convert Calendar.DAY_OF_WEEK to a string representation (e.g., "MONDAY")
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "_sunday"
            Calendar.MONDAY -> "_monday"
            Calendar.TUESDAY -> "_tuesday"
            Calendar.WEDNESDAY -> "_wednesday"
            Calendar.THURSDAY -> "_thursday"
            Calendar.FRIDAY -> "_friday"
            Calendar.SATURDAY -> "_saturday"
            else -> throw IllegalArgumentException("Invalid day of week: $dayOfWeek")
        }
    }
}