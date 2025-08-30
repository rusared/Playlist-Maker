package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface PreferencesRepository {
    fun getSearchHistory(): List<Track>
    fun saveSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()

    fun getDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}