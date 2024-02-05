package com.example.jaago

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class SoundPlayerManager {
    private var soundPlayer: SoundPlayer? = null

    fun play( ringtoneUri: String? , context: Context) {
        stop() // Stop previous sound if it's playing
        soundPlayer = SoundPlayer(context)
        soundPlayer?.play(ringtoneUri , context)
    }

    fun stop() {
        soundPlayer?.stop()
        soundPlayer = null
    }
}


