package com.example.playlistmaker.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerInteractor

class PlayerRepositoryImpl : PlayerInteractor {
    private val mediaPlayer = MediaPlayer()

    override fun prepare(url: String, listener: PlayerInteractor.PlayerStateListener) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { listener.onPrepared() }
        mediaPlayer.setOnCompletionListener { listener.onCompletion() }
    }

    override fun start() = mediaPlayer.start()
    override fun pause() = mediaPlayer.pause()
    override fun release() = mediaPlayer.release()
    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition
    override fun isPlaying(): Boolean = mediaPlayer.isPlaying
}