package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(): List<Track> {
        return searchHistoryRepository.getHistory()
    }

    override fun addToHistory(track: Track) {
        searchHistoryRepository.addToHistory(track)
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }
}