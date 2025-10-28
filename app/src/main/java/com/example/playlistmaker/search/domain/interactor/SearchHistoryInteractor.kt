package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}