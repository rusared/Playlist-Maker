package com.example.playlistmaker.settings.domain.repository

import com.example.playlistmaker.search.domain.model.Track

interface PreferencesRepository {
    fun getSearchHistory(): List<Track>
    fun saveSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()
}

