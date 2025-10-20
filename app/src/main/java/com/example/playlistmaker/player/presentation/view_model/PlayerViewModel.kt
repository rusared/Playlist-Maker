package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerInteractor.PlayerState>()
    val observePlayerState: LiveData<PlayerInteractor.PlayerState> = playerState

    private val isPlaying = MutableLiveData<Boolean>()
    val observeIsPlaying: LiveData<Boolean> = isPlaying

    private val playbackProgress = MutableLiveData<String>()
    val observePlaybackProgress: LiveData<String> = playbackProgress

    private val trackDuration = MutableLiveData<String>()
    val observeTrackDuration: LiveData<String> = trackDuration

    private lateinit var currentTrack: Track

    fun setTrack(track: Track) {
        currentTrack = track
        trackDuration.postValue(formatTime(track.trackTimeMillis.toIntOrNull() ?: 0))
    }

    fun preparePlayer() {
        playerInteractor.prepare(currentTrack.previewUrl, object : PlayerInteractor.PlayerStateListener {
            override fun onStateChanged(state: PlayerInteractor.PlayerState) {
                playerState.postValue(state)
                isPlaying.postValue(state == PlayerInteractor.PlayerState.PLAYING)
                if (state == PlayerInteractor.PlayerState.COMPLETED) {
                    playbackProgress.postValue(formatTime(0))
                }
            }

            override fun onProgressUpdated(position: Int) {
                playbackProgress.postValue(formatTime(position))
            }
        })
    }

    fun playPause() {
        playerInteractor.playPause()
    }

    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }
}