package com.example.playlistmaker.domain.api

interface PlayerInteractor {
    fun prepare(url: String, listener: PlayerStateListener)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean

    interface PlayerStateListener {
        fun onPrepared()
        fun onCompletion()
    }
}