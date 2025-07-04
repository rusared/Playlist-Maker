package com.example.playlistmaker.data

import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory() {

    lateinit var preferencesManager: PreferencesManager
    private val listType = object : TypeToken<MutableList<Track>>() {}.type

    private fun saveHistory(list: MutableList<Track>) {
        val json = Gson().toJson(list)
        preferencesManager.searchHistoryList = json
    }

    fun getHistory(): ArrayList<Track> {
        val json = preferencesManager.searchHistoryList
        return Gson().fromJson(json, listType) ?: arrayListOf()
    }

    fun addToHistory(track: Track) {
        val currentHistory = getHistory()
        val trackToRemove = currentHistory.find {it.trackId == track.trackId }
        if (trackToRemove != null) currentHistory.remove(trackToRemove)
        currentHistory.add(0, track)
        if (currentHistory.size == 11) currentHistory.removeAt(10)
        saveHistory(currentHistory)
    }

    fun clearHistory() {
        val currentHistory = getHistory()
        currentHistory.clear()
        saveHistory(currentHistory)
    }
}