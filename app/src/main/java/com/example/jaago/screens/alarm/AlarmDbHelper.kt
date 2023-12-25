package com.example.jaago.screens.alarm

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AlarmDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "alarm_database"
        const val DATABASE_VERSION = 2

        const val TABLE_NAME = "alarms"
        const val COLUMN_ID = "_id"
        const val COLUMN_TIME = "time"
        const val COLUMN_IS_CHECKED = "is_checked"
    }

    // Create the alarms table
    private val CREATE_TABLE_QUERY = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_TIME TEXT,
            $COLUMN_IS_CHECKED INTEGER DEFAULT 0
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}