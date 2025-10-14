package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerInteractor

class PlayerRepositoryImpl : PlayerInteractor {
    private val mediaPlayer = MediaPlayer()

    override fun prepare(url: String, listener: PlayerInteractor.PlayerStateListener) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { listener.onStateChanged(PlayerInteractor.PlayerState.PREPARED) }
        mediaPlayer.setOnCompletionListener { listener.onStateChanged(PlayerInteractor.PlayerState.COMPLETED) }
    }

    override fun start() = mediaPlayer.start()
    override fun pause() = mediaPlayer.pause()
    override fun release() = mediaPlayer.release()
    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition
    override fun isPlaying(): Boolean = mediaPlayer.isPlaying
    override fun playPause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }
}