package com.example.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.PreferencesManager
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.models.Track

class HistoryInteractorImpl(
    private val searchHistory: SearchHistory,
    private val preferencesManager: PreferencesManager
) : HistoryInteractor {

    private var listener: (() -> Unit)? = null
    private val sharedPreferencesChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PreferencesManager.Companion.SEARCH_HISTORY_LIST_KEY) {
                listener?.invoke()
            }
        }

    init {
        preferencesManager.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)
    }

    override fun getHistory(): List<Track> = searchHistory.getHistory()

    override fun addToHistory(track: Track) = searchHistory.addToHistory(track)

    override fun clearHistory() = searchHistory.clearHistory()

    override fun registerHistoryListener(listener: () -> Unit) {
        this.listener = listener
    }

    override fun unregisterHistoryListener() {
        preferencesManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)
        listener = null
    }
}