package com.example.playlistmaker.domain.impl

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.api.PlayerInteractor

class PlayerInteractorImpl(
    private val repository: PlayerInteractor,
    private val handler: Handler = Handler(Looper.getMainLooper())
) {
    private var progressRunnable: Runnable? = null
    private var listener: PlayerStateListener? = null

    fun prepare(url: String, listener: PlayerStateListener) {
        this.listener = listener
        repository.prepare(url, object : PlayerInteractor.PlayerStateListener {
            override fun onPrepared() {
                listener.onStateChanged(PlayerState.PREPARED)
            }
            override fun onCompletion() {
                stopProgressUpdates()
                listener.onStateChanged(PlayerState.COMPLETED)
            }
        })
    }

    fun playPause() {
        if (repository.isPlaying()) {
            repository.pause()
            stopProgressUpdates()
            listener?.onStateChanged(PlayerState.PAUSED)
        } else {
            repository.start()
            startProgressUpdates()
            listener?.onStateChanged(PlayerState.PLAYING)
        }
    }

    fun release() {
        stopProgressUpdates()
        repository.release()
    }

    private fun startProgressUpdates() {
        progressRunnable = object : Runnable {
            override fun run() {
                listener?.onProgressUpdated(repository.getCurrentPosition())
                handler.postDelayed(this, 300L)
            }
        }.also { handler.post(it) }
    }

    private fun stopProgressUpdates() {
        progressRunnable?.let { handler.removeCallbacks(it) }
        progressRunnable = null
    }

    interface PlayerStateListener {
        fun onStateChanged(state: PlayerState)
        fun onProgressUpdated(position: Int)
    }

    enum class PlayerState {
        PREPARED, PLAYING, PAUSED, COMPLETED
    }
}