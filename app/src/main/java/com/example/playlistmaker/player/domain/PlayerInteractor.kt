package com.example.playlistmaker.player.domain

interface PlayerInteractor {
    fun prepare(url: String, listener: PlayerStateListener)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun playPause()

    interface PlayerStateListener {
        fun onStateChanged(state: PlayerState)
        fun onProgressUpdated(position: Int)
    }

    enum class PlayerState {
        PREPARED, PLAYING, PAUSED, COMPLETED
    }
}