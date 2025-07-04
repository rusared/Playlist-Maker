package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun getHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
    fun registerHistoryListener(listener: () -> Unit)
    fun unregisterHistoryListener()
}