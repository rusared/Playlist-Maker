package com.example.playlistmaker.domain.impl

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.api.PreferencesRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractor(
    private val preferencesRepository: PreferencesRepository
) {
    private val handler = Handler(Looper.getMainLooper())

    fun getHistory(): List<Track> {
        return preferencesRepository.getSearchHistory()
    }

    fun addToHistory(track: Track) {
        handler.postDelayed({
            val currentHistory = preferencesRepository.getSearchHistory().toMutableList()

            currentHistory.removeAll { it.trackId == track.trackId }
            currentHistory.add(0, track)
            if (currentHistory.size > 10) {
                currentHistory.removeAt(10)
            }
            preferencesRepository.saveSearchHistory(currentHistory)
        }, 500)

    }

    fun clearHistory() {
        preferencesRepository.clearSearchHistory()
    }
}