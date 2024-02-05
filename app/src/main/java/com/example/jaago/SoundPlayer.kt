package com.example.jaago

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class SoundPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun play(ringtoneUri: String? , context: Context) {
        stop() // Stop previous sound if it's playing
        if (ringtoneUri != null) {
            mediaPlayer = MediaPlayer.create(context, Uri.parse(ringtoneUri))
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else {
            // If no ringtone URI is provided, play the default sound
            mediaPlayer = MediaPlayer.create(context, R.raw.test1)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}