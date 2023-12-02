package com.example.jaago.screens.timer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentLayout = findViewById<LinearLayout>(R.id.fragment_layout)
        layoutInflater.inflate(R.layout.activity_timer , fragmentLayout)

        init()
        setUpNumberPicker()
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
                Toast.makeText(mContext, "Time Completed", Toast.LENGTH_SHORT).show()
            }
        }.start()
        isTimerRunning = true
        updateButtonVisibility()
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
    }

}