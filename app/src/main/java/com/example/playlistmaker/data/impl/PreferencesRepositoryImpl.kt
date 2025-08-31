package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.AppPreferences
import com.example.playlistmaker.domain.api.PreferencesRepository
import com.example.playlistmaker.domain.api.ThemeSettings
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesRepositoryImpl(
    private val appPreferences: AppPreferences
) : PreferencesRepository {

    private val gson = Gson()
    private val listType = object : TypeToken<List<Track>>() {}.type

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            isThemeSet = appPreferences.isThemeSet,
            isDarkTheme = appPreferences.isDarkTheme
        )
    }

    override fun saveThemeSettings(settings: ThemeSettings) {
        appPreferences.isThemeSet = settings.isThemeSet
        appPreferences.isDarkTheme = settings.isDarkTheme
    }

    override fun getSearchHistory(): List<Track> {
        val json = appPreferences.searchHistoryList ?: ""
        return gson.fromJson(json, listType) ?: emptyList()
    }

    override fun saveSearchHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        appPreferences.searchHistoryList = json
    }

    override fun clearSearchHistory() {
        appPreferences.searchHistoryList = ""
    }
}