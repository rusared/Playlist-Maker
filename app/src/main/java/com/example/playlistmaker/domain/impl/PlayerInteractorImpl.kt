package com.example.playlistmaker.domain.impl

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.api.PlayerInteractor

class PlayerInteractorImpl(
    private val repository: PlayerInteractor
) : PlayerInteractor {

    private val handler = Handler(Looper.getMainLooper())
    private var progressRunnable: Runnable? = null
    private var stateListener: PlayerInteractor.PlayerStateListener? = null

    override fun prepare(url: String, listener: PlayerInteractor.PlayerStateListener) {
        this.stateListener = listener
        repository.prepare(url, object : PlayerInteractor.PlayerStateListener {
            override fun onStateChanged(state: PlayerInteractor.PlayerState) {
                stateListener?.onStateChanged(state)
            }
            override fun onProgressUpdated(position: Int) {
                stateListener?.onProgressUpdated(position)
            }
        })
    }

    override fun start() {
        repository.start()
        startProgressUpdates()
        stateListener?.onStateChanged(PlayerInteractor.PlayerState.PLAYING)
    }

    override fun pause() {
        repository.pause()
        stopProgressUpdates()
        stateListener?.onStateChanged(PlayerInteractor.PlayerState.PAUSED)
    }

    override fun release() {
        stopProgressUpdates()
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

    private fun startProgressUpdates() {
        progressRunnable = object : Runnable {
            override fun run() {
                stateListener?.onProgressUpdated(repository.getCurrentPosition())
                handler.postDelayed(this, 300L)
            }
        }.also { handler.post(it) }
    }

    private fun stopProgressUpdates() {
        progressRunnable?.let { handler.removeCallbacks(it) }
        progressRunnable = null
    }
}