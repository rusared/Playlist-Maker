package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface PreferencesRepository {
    fun getThemeSettings(): ThemeSettings
    fun saveThemeSettings(settings: ThemeSettings)
    fun getSearchHistory(): List<Track>
    fun saveSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()
}

data class ThemeSettings(
    val isThemeSet: Boolean,
    val isDarkTheme: Boolean
)