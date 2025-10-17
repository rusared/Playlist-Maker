package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryInteractor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    fun getHistory(): List<Track> {
        return searchHistoryRepository.getHistory()
    }

    fun addToHistory(track: Track) {
        searchHistoryRepository.addToHistory(track)
    }

    fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }
}