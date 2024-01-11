package com.example.jaago.screens.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.MyApplication
import com.example.jaago.R
import com.example.jaago.SoundPlayerManager
import com.example.jaago.model.AlarmItem
import com.example.jaago.model.MathQuestion
import com.example.jaago.screens.base.BaseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class AlarmActivity : BaseActivity() {
    private lateinit var addButton: FloatingActionButton
    private lateinit var dbHelper: AlarmDbHelper
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarms: MutableList<AlarmItem>
    private lateinit var soundPlayerManager: SoundPlayerManager
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

        soundPlayerManager = (application as MyApplication).soundPlayerManager
    }

    private fun startAddAlarmActivityWithPreSelection(position: Int) {
        val selectedAlarm = alarms[position]
        val intent = Intent(this, AddAlarm::class.java).apply {
            putExtra(AddAlarm.SELECTED_ID, selectedAlarm.id)
            putExtra(AddAlarm.SELECTED_TIME, selectedAlarm.time)
            putExtra(AddAlarm.SELECTED_DAYS, selectedAlarm.selectedDays.toTypedArray())
            putExtra(AddAlarm.PUZZLE , selectedAlarm.puzzleType)
            putExtra(AddAlarm.SELECTED_SEEK_BAR_VALUE , selectedAlarm.seekBarDifficulty)
            putExtra(AddAlarm.SELECTED_REPETITIONS , selectedAlarm.repetitions)
        }
        startActivityForResult(intent, TIME_SELECTION_REQUEST_CODE)
    }
    private fun getAllAlarms(): List<AlarmItem> {
        val alarms = mutableListOf<AlarmItem>()
        val db = dbHelper.writableDatabase
        val cursor: Cursor? = db.query(
            AlarmDbHelper.TABLE_NAME,
            arrayOf( AlarmDbHelper.COLUMN_ID, AlarmDbHelper.COLUMN_TIME, AlarmDbHelper.COLUMN_IS_CHECKED , AlarmDbHelper.COLUMN_SELECTED_DAYS , AlarmDbHelper.COLUMN_PUZZLE , AlarmDbHelper.COLUMN_SEEK_BAR_VALUE , AlarmDbHelper.COLUMN_REPETITIONS),
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
            val puzzle = cursor.getString(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_PUZZLE))
            val seekBarValue = cursor.getString(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_SEEK_BAR_VALUE))
            val repetitions = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_REPETITIONS))
            val alarmItem = AlarmItem( id , time , selectedDays , isChecked , puzzle , seekBarValue , repetitions )
            alarms.add(alarmItem)
        }
        cursor?.close()
        return alarms
    }


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

        cancelAlarm(idToDelete)
    }

    private fun insertAlarm(
        id: Long,
        time: String, selectedDays: Array<String>?, checked: Boolean, seekBarValue: String?, repetitions: Int?,
        puzzle: String?
    ) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(AlarmDbHelper.COLUMN_ID, id)
            put(AlarmDbHelper.COLUMN_TIME, time)
            put(AlarmDbHelper.COLUMN_SELECTED_DAYS, selectedDays?.joinToString(","))
            put(AlarmDbHelper.COLUMN_IS_CHECKED, checked)
            put(AlarmDbHelper.COLUMN_PUZZLE , puzzle )
            put(AlarmDbHelper.COLUMN_REPETITIONS , repetitions)
            put(AlarmDbHelper.COLUMN_SEEK_BAR_VALUE , seekBarValue)
        }

        db.insertWithOnConflict(AlarmDbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)

        setUpAlarm(id , time , selectedDays, seekBarValue , repetitions , puzzle )
    }

    private fun cancelAlarm(id: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun setUpAlarm(id: Long,time: String , selectedDays: Array<String>? , seekBarValue: String?, repetitions: Int?,
                           puzzle: String?) {
        // Set up the Alarm using AlarmManager with the alarm ID
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Convert the selected time to milliseconds
        val alarmTimeInMillis = convertTimeToMillis(time)

        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", id)
            putExtra("SELECTED_DAYS" , selectedDays )
            putExtra("PUZZLE" , puzzle )
            putExtra("REPETITIONS", repetitions)
            putExtra("SEEK_BAR_VALUE", seekBarValue)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Set the alarm to trigger at the specified time
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarmTimeInMillis,
            pendingIntent
        )
    }

    private fun convertTimeToMillis(time: String): Long {
        val calendar = Calendar.getInstance()
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TIME_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedId = data?.getLongExtra(AddAlarm.SELECTED_ID, -1) ?: -1
            val selectedTime = data?.getStringExtra(AddAlarm.SELECTED_TIME)
            val selectedDays = data?.getStringArrayExtra(AddAlarm.SELECTED_DAYS)
            val seekBarValue = data?.getStringExtra(AddAlarm.SEEK_BAR_VALUE)
            val repetitions = data?.getIntExtra(AddAlarm.NUMBER_PICKER_VALUE , 1)
            val puzzle = data?.getStringExtra(AddAlarm.PUZZLE)

            selectedTime?.let {
                // Save the new alarm to the database
                insertAlarm( selectedId ,it, selectedDays , true , seekBarValue , repetitions , puzzle )
                alarmAdapter.addAlarm(AlarmItem(selectedId ,it, selectedDays?.toList() ?: emptyList() , true , puzzle , seekBarValue , repetitions ))
            }

            if (selectedId != -1L) {
                // Update existing alarm
                updateAlarm(selectedId, selectedTime, selectedDays, true , seekBarValue , repetitions , puzzle)
            } else {
                // Add new alarm
                val uniqueId = System.currentTimeMillis()
                if (selectedTime != null) {
                    insertAlarm(uniqueId, selectedTime, selectedDays, true ,  seekBarValue , repetitions , puzzle )
                    alarmAdapter.addAlarm(AlarmItem(uniqueId, selectedTime, selectedDays?.toList() ?: emptyList(), true  , puzzle , seekBarValue , repetitions ))
                }
            }
        }
    }
    private fun updateAlarm(id: Long, time: String?, selectedDays: Array<String>?, checked: Boolean , seekBarValue: String?, repetitions: Int?,
                            puzzle: String?) {
        cancelAlarm(id)
        // Implement the logic to update an existing alarm
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(AlarmDbHelper.COLUMN_TIME, time)
            put(AlarmDbHelper.COLUMN_SELECTED_DAYS, selectedDays?.joinToString(","))
            put(AlarmDbHelper.COLUMN_IS_CHECKED, checked)
            put(AlarmDbHelper.COLUMN_PUZZLE , puzzle)
            put(AlarmDbHelper.COLUMN_REPETITIONS , repetitions)
            put(AlarmDbHelper.COLUMN_SEEK_BAR_VALUE , seekBarValue)
        }
        db.update(
            AlarmDbHelper.TABLE_NAME,
            values,
            "${AlarmDbHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        // You might need to update the AlarmItem in your alarms list as well
        val updatedAlarm = alarms.find { it.id == id }
        updatedAlarm?.apply {
            this.time = time ?: ""
            this.selectedDays = selectedDays?.toList() ?: emptyList()
            this.isChecked = checked
            this.repetitions = repetitions
            this.seekBarDifficulty = seekBarValue
            this.puzzleType = puzzle
        }
        val updatedAlarmItem = alarms.find { it.id == id }
        updatedAlarmItem?.let {
            setUpAlarm(id, it.time, selectedDays , seekBarValue , repetitions , puzzle )
        }
        alarmAdapter.notifyDataSetChanged()
    }
    override fun onResume() {
        super.onResume()
        alarms.clear()
        alarms.addAll(getAllAlarms())
        alarmAdapter.notifyDataSetChanged()
    }
}