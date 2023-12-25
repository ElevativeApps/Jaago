package com.example.jaago.screens.alarm

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.R
import com.example.jaago.model.AlarmItem
import com.example.jaago.screens.base.BaseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AlarmActivity : BaseActivity() {
    private lateinit var addButton: FloatingActionButton
    private lateinit var dbHelper: AlarmDbHelper
    private lateinit var alarmAdapter: AlarmAdapter
    companion object {
        const val TIME_SELECTION_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentLayout = findViewById<LinearLayout>(R.id.fragment_layout)
        layoutInflater.inflate(R.layout.activity_alarm , fragmentLayout)

        addButton = findViewById(R.id.btn_add)
        addButton.setOnClickListener{
            val intent = Intent(this , AddAlarm::class.java)
            startActivityForResult(intent , TIME_SELECTION_REQUEST_CODE)
        }
        dbHelper = AlarmDbHelper(this)
        // Fetch and display existing alarms
        val alarms = getAllAlarms().toMutableList()
        val recyclerView = findViewById<RecyclerView>(R.id.rv_alarm)
        recyclerView.layoutManager = LinearLayoutManager(this)
        alarmAdapter = AlarmAdapter(alarms)
        recyclerView.adapter = alarmAdapter
    }

    private fun getAllAlarms(): List<AlarmItem> {
        val alarms = mutableListOf<AlarmItem>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            AlarmDbHelper.TABLE_NAME,
            arrayOf(AlarmDbHelper.COLUMN_TIME, AlarmDbHelper.COLUMN_IS_CHECKED),
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val time = cursor.getString(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_TIME))
            val isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_IS_CHECKED)) == 1
            val alarmItem = AlarmItem(time, isChecked)
            alarms.add(alarmItem)
        }
        cursor.close()
        return alarms
    }

    private fun insertAlarm(time: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(AlarmDbHelper.COLUMN_TIME, time)
        }
        db.insert(AlarmDbHelper.TABLE_NAME, null, values)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TIME_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedTime = data?.getStringExtra(AddAlarm.SELECTED_TIME)
            selectedTime?.let {
                // Save the new alarm to the database
                insertAlarm(it)
                alarmAdapter.addAlarm(AlarmItem(it, false))
            }
        }
    }
}