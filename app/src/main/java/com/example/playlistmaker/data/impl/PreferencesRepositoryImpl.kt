package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.PreferencesManager
import com.example.playlistmaker.domain.api.PreferencesRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesRepositoryImpl(
    private val preferencesManager: PreferencesManager
) : PreferencesRepository {

    private val gson = Gson()
    private val listType = object : TypeToken<List<Track>>() {}.type

    override fun getSearchHistory(): List<Track> {
        val json = preferencesManager.searchHistoryList ?: ""
        return gson.fromJson(json, listType) ?: emptyList()
    }

    override fun saveSearchHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        preferencesManager.searchHistoryList = json
    }

    override fun clearSearchHistory() {
        preferencesManager.searchHistoryList = ""
    }

    override fun getDarkThemeEnabled(): Boolean {
        return preferencesManager.isDarkTheme
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        preferencesManager.isDarkTheme = enabled
    }
}