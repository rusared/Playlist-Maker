package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.settings.domain.repository.PreferencesRepository
import com.example.playlistmaker.settings.data.datasource.AppPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesRepositoryImpl(
    private val appPreferences: AppPreferences
) : PreferencesRepository {

    private val gson = Gson()
    private val listType = object : TypeToken<List<Track>>() {}.type

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