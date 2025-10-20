package com.example.playlistmaker.player.domain.repository

import com.example.playlistmaker.player.domain.interactor.PlayerInteractor

interface PlayerRepository {
    fun prepare(url: String, listener: PlayerInteractor.PlayerStateListener)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun playPause()
}