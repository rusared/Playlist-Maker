package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    override fun prepare(url: String, listener: PlayerInteractor.PlayerStateListener) {
        repository.prepare(url, object : PlayerInteractor.PlayerStateListener {
            override fun onStateChanged(state: PlayerInteractor.PlayerState) {
                listener.onStateChanged(state)
            }
            override fun onProgressUpdated(position: Int) {
                listener.onProgressUpdated(position)
            }
        })
    }

    override fun start() {
        repository.start()
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun getCurrentPosition(): Int = repository.getCurrentPosition()

    override fun isPlaying(): Boolean = repository.isPlaying()

    override fun playPause() {
        if (isPlaying()) {
            pause()
        } else {
            start()
        }
    }
}