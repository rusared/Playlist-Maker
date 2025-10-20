package com.example.playlistmaker.search.data.repository

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.settings.domain.repository.PreferencesRepository

class SearchHistoryRepositoryImpl(
    private val preferencesRepository: PreferencesRepository
) : SearchHistoryRepository {

    private val handler = Handler(Looper.getMainLooper())

    override fun getHistory(): List<Track> {
        return preferencesRepository.getSearchHistory()
    }

    override fun addToHistory(track: Track) {
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

    override fun clearHistory() {
        preferencesRepository.clearSearchHistory()
    }
}