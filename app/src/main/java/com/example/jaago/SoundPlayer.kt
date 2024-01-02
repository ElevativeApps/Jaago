package com.example.jaago

import android.content.Context
import android.media.MediaPlayer

class SoundPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun play() {
        stop() // Stop previous sound if it's playing
        mediaPlayer = MediaPlayer.create(context, R.raw.test1)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
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