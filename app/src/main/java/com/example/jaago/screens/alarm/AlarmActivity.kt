package com.example.jaago.screens.alarm

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Toast
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
    private lateinit var alarms: MutableList<AlarmItem>
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
        alarms = getAllAlarms().toMutableList()
        val recyclerView = findViewById<RecyclerView>(R.id.rv_alarm)
        recyclerView.layoutManager = LinearLayoutManager(this)
        alarmAdapter = AlarmAdapter(alarms,
            onDeleteClickListener = { position ->
                // Handle delete click event
                deleteAlarm(position)
            },
            onItemClick = { position ->
                // Handle item click event
                startAddAlarmActivityWithPreSelection(position)
            } ,
            this
        )
//        alarmAdapter = AlarmAdapter(alarms)
        recyclerView.adapter = alarmAdapter
    }

    private fun startAddAlarmActivityWithPreSelection(position: Int) {
        val selectedAlarm = alarms[position]
        val intent = Intent(this, AddAlarm::class.java).apply {
            putExtra(AddAlarm.SELECTED_TIME, selectedAlarm.time)
            putExtra(AddAlarm.SELECTED_DAYS, selectedAlarm.selectedDays.toTypedArray())
        }
        startActivityForResult(intent, TIME_SELECTION_REQUEST_CODE)
    }
    private fun getAllAlarms(): List<AlarmItem> {
        val alarms = mutableListOf<AlarmItem>()
        val db = dbHelper.writableDatabase
        val cursor: Cursor? = db.query(
            AlarmDbHelper.TABLE_NAME,
            arrayOf( AlarmDbHelper.COLUMN_ID, AlarmDbHelper.COLUMN_TIME, AlarmDbHelper.COLUMN_IS_CHECKED , AlarmDbHelper.COLUMN_SELECTED_DAYS),
            null,
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext() == true) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_ID))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_TIME))
            val isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_IS_CHECKED)) == 1
            val selectedDaysString = cursor.getString(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_SELECTED_DAYS)) ?: ""
            val selectedDays = selectedDaysString.split(",").toList()
            val alarmItem = AlarmItem( id , time , selectedDays , isChecked)
            alarms.add(alarmItem)
        }
        cursor?.close()
        return alarms
    }

//    private fun deleteAlarm(position: Int) {
//        // Delete alarm from the database
//        val db = dbHelper.writableDatabase
//        val timeToDelete = alarms[position].time
//        db.delete(
//            AlarmDbHelper.TABLE_NAME,
//            "${AlarmDbHelper.COLUMN_TIME} = ?",
//            arrayOf(timeToDelete)
//        )
//
//        // Remove alarm from the list and update the adapter
//        alarms.removeAt(position)
//        alarmAdapter.notifyItemRemoved(position)
//    }

    private fun deleteAlarm(position: Int) {
        // Delete alarm from the database
        val db = dbHelper.writableDatabase
        val idToDelete = alarms[position].id // Assuming you have an 'id' property in your AlarmItem class
        db.delete(
            AlarmDbHelper.TABLE_NAME,
            "${AlarmDbHelper.COLUMN_ID} = ?",
            arrayOf(idToDelete.toString())
        )

        // Remove alarm from the list and update the adapter
        alarms.removeAt(position)
        alarmAdapter.notifyItemRemoved(position)
    }

    private fun insertAlarm(id: Long,time: String , selectedDays: Array<String>? , checked: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(AlarmDbHelper.COLUMN_ID, id)
            put(AlarmDbHelper.COLUMN_TIME, time)
            put(AlarmDbHelper.COLUMN_SELECTED_DAYS, selectedDays?.joinToString(","))
            put(AlarmDbHelper.COLUMN_IS_CHECKED, checked)
        }
        db.insert(AlarmDbHelper.TABLE_NAME, null, values)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TIME_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedId = data?.getLongExtra(AddAlarm.SELECTED_ID, -1) ?: -1
            val selectedTime = data?.getStringExtra(AddAlarm.SELECTED_TIME)
            val selectedDays = data?.getStringArrayExtra(AddAlarm.SELECTED_DAYS)
            selectedTime?.let {
                // Save the new alarm to the database
                insertAlarm( selectedId ,it, selectedDays , true )
                alarmAdapter.addAlarm(AlarmItem(selectedId ,it, selectedDays?.toList() ?: emptyList() , true ))
            }
        }
    }
}