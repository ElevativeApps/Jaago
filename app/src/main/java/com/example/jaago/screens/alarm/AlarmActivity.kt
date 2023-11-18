package com.example.jaago.screens.alarm

import android.os.Bundle
import android.widget.LinearLayout
import com.example.jaago.R
import com.example.jaago.screens.base.BaseActivity

class AlarmActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentLayout = findViewById<LinearLayout>(R.id.fragment_layout)
        layoutInflater.inflate(R.layout.activity_alarm , fragmentLayout)
    }
}