package com.example.jaago.screens.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateUtils.getDayOfWeekString
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.legacy.content.WakefulBroadcastReceiver
import com.example.jaago.MyApplication
import com.example.jaago.R
import com.example.jaago.screens.maths.MathsQna
import com.example.jaago.screens.shake.ShakeActivity
import com.example.jaago.screens.typing.TypingActivity
import java.util.*

class AlarmReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Handle the alarm event, show the notification, or start the activity
        val alarmId = intent?.getLongExtra("ALARM_ID", -1L) ?: -1L
        val selectedDays = intent?.getStringArrayExtra("SELECTED_DAYS")
        val seekBarValue = intent?.getStringExtra("SEEK_BAR_VALUE")
        val repetitions = intent?.getIntExtra("REPETITIONS", 1)
        val puzzle = intent?.getStringExtra("PUZZLE")
        val shakeRepetitions = intent?.getIntExtra("SHAKE_REPETITIONS" , 2)
        val selectedSentence = intent?.getStringExtra("SELECTED_SENTENCE")
        val selectedRingtone = intent?.getStringExtra("SELECTED_RINGTONE")
        Log.d("days_test_1", "${selectedDays?.contentToString()}")
        Log.d("seekBarValue_ar", "$seekBarValue")
        Log.d("repetitions_ar", "$repetitions")
        Log.d("shake_repetitions", "$shakeRepetitions")
        Log.d("selected_sentence" , "$selectedSentence")
        Log.d("selected_reason" , "$selectedRingtone")
        if (alarmId != -1L) {
            // Show notification
            if( shouldRingToday( selectedDays ) ){
                showNotification(context!!, alarmId, selectedDays, seekBarValue, repetitions, puzzle , shakeRepetitions , selectedSentence , selectedRingtone)
            }

            // Complete the wakeful work
            completeWakefulIntent(intent)
        }
    }

    private fun showNotification(
        context: Context,
        alarmId: Long,
        selectedDays: Array<String>?,
        seekBarValue: String?,
        repetitions: Int?,
        puzzle: String?,
        shakeRepetitions: Int?,
        selectedSentence: String?,
        selectedRingtone: String?
    ) {
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
        var pendingIntent: PendingIntent? = null
        if (puzzle == "MATHS_PUZZLE") {
            val mathsQnaIntent = Intent(context, MathsQna::class.java).apply {
                putExtra("ALARM_ID", alarmId)
                putExtra("SEEK_BAR_VALUE", seekBarValue)
                putExtra("REPETITIONS", repetitions)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            pendingIntent = PendingIntent.getActivity(
                context,
                alarmId.toInt(),
                mathsQnaIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else if (puzzle == "SHAKE_PUZZLE") {
            val shakeActivityIntent = Intent(context, ShakeActivity::class.java).apply {
                putExtra("ALARM_ID", alarmId)
                putExtra("SHAKE_REPETITIONS", shakeRepetitions)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            pendingIntent = PendingIntent.getActivity(
                context,
                alarmId.toInt(),
                shakeActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else if ( puzzle == "TYPING_PUZZLE") {
            val shakeActivityIntent = Intent(context, TypingActivity::class.java).apply {
                putExtra("ALARM_ID", alarmId)
                putExtra("SELECTED_SENTENCE", selectedSentence)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            pendingIntent = PendingIntent.getActivity(
                context,
                alarmId.toInt(),
                shakeActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

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

//        if (selectedDays != null && selectedDays.isNotEmpty()) {
//            // Get the current day of the week
//            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
//            val currentDayString = getDayOfWeekString(currentDay)
//
//            if (selectedDays.contains(currentDayString)) {
//
//                val soundPlayerManager =
//                    (context.applicationContext as MyApplication).soundPlayerManager
//                soundPlayerManager.play(context)
//                notificationManager.notify(alarmId.toInt(), notificationBuilder.build())
//            }
//        } else {
//            val soundPlayerManager =
//                (context.applicationContext as MyApplication).soundPlayerManager
//            soundPlayerManager.play(context)
//            notificationManager.notify(alarmId.toInt(), notificationBuilder.build())
//        }

        val soundPlayerManager = (context.applicationContext as MyApplication).soundPlayerManager
        soundPlayerManager.play(selectedRingtone, context)
        notificationManager.notify(alarmId.toInt(), notificationBuilder.build())
    }

    private fun getDayOfWeekString(dayOfWeek: Int): String {
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
    private fun shouldRingToday(selectedDays: Array<String>?): Boolean {
        if (selectedDays != null && selectedDays.isNotEmpty()) {
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val currentDayString = getDayOfWeekString(currentDay)
            Log.d("currentDay", "$currentDay")
            Log.d("currentDayString", "$currentDayString")
            return selectedDays.contains(currentDayString)
        }
        return false
    }
}