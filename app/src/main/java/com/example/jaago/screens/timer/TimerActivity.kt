package com.example.jaago.screens.timer

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.jaago.R
import com.example.jaago.screens.base.BaseActivity

class TimerActivity : BaseActivity(), View.OnClickListener {

    lateinit var mContext: Context
    private lateinit var timerDisplay: TextView
    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var secondPicker: NumberPicker
    private lateinit var timerPicker: LinearLayout
    private lateinit var btnPlay: ImageView
    private lateinit var btnPause: ImageView
    private lateinit var btnReset: ImageView
    private lateinit var btnCancel: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var flTimer : FrameLayout
    private var countDownTimer: CountDownTimer? = null
    private var totalTimeInMillis: Long = 0
    private var isTimerRunning = false
    private var remainingTimeInMillis: Long = 0
    private lateinit var mediaPlayer: MediaPlayer
    lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationChannelId: String
    val notificationId = 1
    private val MY_PERMISSIONS_REQUEST_VIBRATE = 123

    companion object {
        var instance: TimerActivity? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentLayout = findViewById<LinearLayout>(R.id.fragment_layout)
        layoutInflater.inflate(R.layout.activity_timer , fragmentLayout)
        instance = this
        init()
        setUpNumberPicker()
//        notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager = NotificationManagerCompat.from(this)
        notificationChannelId = "timer_channel"
        createNotificationChannel()


        // Create notification channel for Android Oreo and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            val channel = NotificationChannel(
////                notificationChannelId,
////                "Timer Notification Channel",
////                NotificationManager.IMPORTANCE_HIGH
////            )
////            notificationManager.createNotificationChannel(channel)
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val mChannel = NotificationChannel(notificationChannelId, name, importance)
//            mChannel.description = descriptionText
//            // Register the channel with the system. You can't change the importance
//            // or other notification behaviors after this.
////            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(mChannel)
//        }
    }
    private fun init(){
        mContext = this
        timerDisplay = findViewById(R.id.timerDisplay)
        hourPicker = findViewById(R.id.hourPicker)
        minutePicker = findViewById(R.id.minutePicker)
        secondPicker = findViewById(R.id.secondPicker)
        timerPicker = findViewById(R.id.ll_timer)
        btnPlay = findViewById(R.id.btn_play)
        btnPause = findViewById(R.id.btn_pause)
        btnReset = findViewById(R.id.btn_reset)
        btnCancel = findViewById(R.id.btn_cancel)
        progressBar = findViewById(R.id.pb_timer)
        flTimer = findViewById(R.id.fl_timer)

    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer Notification Channel"
            val descriptionText = "Channel for timer notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(notificationChannelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setUpNumberPicker() {

        hourPicker.minValue = 0
        hourPicker.maxValue = 23

        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        secondPicker.minValue = 0
        secondPicker.maxValue = 59

        val formatter : NumberPicker.Formatter = NumberPicker.Formatter { String.format("%02d",it) }
        minutePicker.setFormatter( formatter )
        hourPicker.setFormatter( formatter )
        secondPicker.setFormatter( formatter )

        btnPlay.setOnClickListener {
            timerDisplay.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            flTimer.visibility = View.VISIBLE
            timerPicker.visibility = View.GONE
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer(remainingTimeInMillis)
            }
        }

        btnPause.setOnClickListener{
            pauseTimer()
        }

        btnReset.setOnClickListener {
            resetTimer()
        }

        btnCancel.setOnClickListener{
            cancelTimer()
        }
    }
    override fun onClick(v: View?) {
        when ( v!!.id ){
            R.id.btn_play -> {
                timerDisplay.visibility = View.VISIBLE
                timerPicker.visibility = View.GONE
                if (isTimerRunning) {
                    pauseTimer()
                } else {
                    startTimer(remainingTimeInMillis)
                }
            }
            R.id.btn_cancel -> {

            }
            R.id.btn_reset -> {

            }
            R.id.btn_pause -> {
                pauseTimer()
            }
        }
    }
    private fun calculateTotalTime(): Long {
        val hours = hourPicker.value
        val minutes = minutePicker.value
        val seconds = secondPicker.value
        totalTimeInMillis = ((hours * 3600 + minutes * 60 + seconds) * 1000).toLong()
        return totalTimeInMillis
    }

    private fun startTimer(initialTime: Long) {
        remainingTimeInMillis = initialTime
        if (remainingTimeInMillis == 0L) {
            remainingTimeInMillis = calculateTotalTime()
        }
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(remainingTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInMillis = millisUntilFinished
                updateTimeDisplay(millisUntilFinished)
                updateProgressBar(millisUntilFinished)
            }

            override fun onFinish() {
                timerDisplay.visibility = View.GONE
                progressBar.visibility = View.GONE
                flTimer.visibility = View.GONE
                timerPicker.visibility = View.VISIBLE
                isTimerRunning = false
                updateButtonVisibility()
                remainingTimeInMillis = 0
                playNotificationSound()
                showNotification()
                Toast.makeText(mContext, "Time Completed", Toast.LENGTH_SHORT).show()
            }
        }.start()
        isTimerRunning = true
        updateButtonVisibility()
    }

    private fun playNotificationSound() {
        // Initialize and start playing the sound
        mediaPlayer = MediaPlayer.create(this, R.raw.test1)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }
    fun stopNotificationSound() {
        // Stop the sound when the timer is stopped or the user presses the "Stop" button
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    fun cancelNotification(){
        notificationManager.cancel(notificationId)
    }
    private fun showNotification() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // You might want to show a rationale to the user and then request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.VIBRATE),
                MY_PERMISSIONS_REQUEST_VIBRATE
            )
        } else {
            // Permission already granted, proceed with your logic

            val notificationIntent = Intent(this, TimerActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val stopIntent = Intent(this, NotificationReceiver::class.java)
            stopIntent.action = "STOP_ACTION"
            val stopPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(this, notificationChannelId)
                .setContentTitle("Timer Completed")
                .setContentText("Click to stop the sound")
                .setSmallIcon(R.drawable.ic_stop_watch)
                .setVibrate(longArrayOf(0))
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE )
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSilent(true)
                .addAction(R.drawable.ic_stop_watch, "Stop", stopPendingIntent)
                .build()

            notificationManager.notify(notificationId, notification)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d("TimerActivity", "onRequestPermissionsResult called")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_VIBRATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with your logic
                    showNotification()
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(
                        this,
                        "Vibration permission is required for this feature.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        updateButtonVisibility()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        updateButtonVisibility()
        remainingTimeInMillis = calculateTotalTime()
        timerPicker.visibility = View.GONE
        updateTimeDisplay(remainingTimeInMillis - 1000)
        updateProgressBar(remainingTimeInMillis)
        timerDisplay.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        flTimer.visibility = View.VISIBLE
    }

    private fun cancelTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        updateButtonVisibility()
        remainingTimeInMillis = 0
        timerPicker.visibility = View.VISIBLE
        timerDisplay.visibility = View.GONE
        progressBar.visibility = View.GONE
        flTimer.visibility = View.GONE
    }

    private fun updateButtonVisibility() {
        if (isTimerRunning) {
            btnPlay.visibility = View.INVISIBLE
            btnPause.visibility = View.VISIBLE
        } else {
            btnPlay.visibility = View.VISIBLE
            btnPause.visibility = View.INVISIBLE
        }
    }

    private fun updateTimeDisplay(millisUntilFinished: Long) {
        val hours = ((millisUntilFinished + 1000) / 3600000).toInt()
        val minutes = (((millisUntilFinished + 1000) % 3600000) / 60000).toInt()
        val seconds = (((millisUntilFinished + 1000) % 60000) / 1000).toInt()

        timerDisplay.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    private fun updateProgressBar(millisUntilFinished: Long) {
        val progress = ((totalTimeInMillis - millisUntilFinished) * 100 / totalTimeInMillis).toInt()
        progressBar.progress = progress
    }
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            stopNotificationSound()
        }
    }

}