package com.example.jaago

import android.app.Application

class MyApplication : Application() {
    lateinit var soundPlayerManager: SoundPlayerManager
        private set

    override fun onCreate() {
        super.onCreate()
        soundPlayerManager = SoundPlayerManager()
    }
}