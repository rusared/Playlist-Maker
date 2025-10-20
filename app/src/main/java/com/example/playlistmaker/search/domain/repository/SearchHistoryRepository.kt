package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}