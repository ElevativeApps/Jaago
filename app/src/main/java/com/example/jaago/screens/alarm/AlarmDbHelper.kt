package com.example.jaago.screens.alarm

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AlarmDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "alarm_database"
        const val DATABASE_VERSION = 6

        const val TABLE_NAME = "alarms"
        const val COLUMN_ID = "_id"
        const val COLUMN_TIME = "time"
        const val COLUMN_IS_CHECKED = "is_checked"
        const val COLUMN_SELECTED_DAYS = "selected_days"
    }

    // Create the alarms table
    private val CREATE_TABLE_QUERY = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_TIME TEXT,
            $COLUMN_SELECTED_DAYS TEXT,
            $COLUMN_IS_CHECKED INTEGER DEFAULT 0,
            UNIQUE ($COLUMN_TIME, $COLUMN_SELECTED_DAYS)
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseCreate", "Creating database")
        db.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseUpgrade", "Upgrading database from version $oldVersion to $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}