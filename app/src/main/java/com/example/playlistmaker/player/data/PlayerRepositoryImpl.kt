package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var progressRunnable: Runnable? = null
    private var progressListener: PlayerInteractor.PlayerStateListener? = null

    override fun prepare(url: String, listener: PlayerInteractor.PlayerStateListener) {
        this.progressListener = listener
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            listener.onStateChanged(PlayerInteractor.PlayerState.PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            stopProgressUpdates()
            listener.onStateChanged(PlayerInteractor.PlayerState.COMPLETED)
        }
    }

    override fun start() {
        mediaPlayer.start()
        startProgressUpdates()
        progressListener?.onStateChanged(PlayerInteractor.PlayerState.PLAYING)
    }

    override fun pause() {
        mediaPlayer.pause()
        stopProgressUpdates()
        progressListener?.onStateChanged(PlayerInteractor.PlayerState.PAUSED)
    }

    override fun release() {
        stopProgressUpdates()
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun playPause() {
        if (mediaPlayer.isPlaying) {
            pause()
        } else {
            start()
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()

        progressRunnable = object : Runnable {
            override fun run() {
                val position = mediaPlayer.currentPosition
                progressListener?.onProgressUpdated(position)
                handler.postDelayed(this, PROGRESS_DEBOUNCE_DELAY)
            }
        }.also { handler.post(it) }
    }

    private fun stopProgressUpdates() {
        progressRunnable?.let { handler.removeCallbacks(it) }
        progressRunnable = null
    }

    companion object {
        private const val PROGRESS_DEBOUNCE_DELAY = 300L
    }
}