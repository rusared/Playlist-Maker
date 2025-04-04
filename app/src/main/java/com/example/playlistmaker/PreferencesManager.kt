package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val IS_DARK_THEME_KEY = "is_dark_theme_key"
        const val SEARCH_HISTORY_LIST_KEY = "search_history_list_key"
    }

    var isDarkTheme: Boolean
        get() = sharedPreferences.getBoolean(IS_DARK_THEME_KEY, false)
        set(value) {
            sharedPreferences.edit()
                .putBoolean(IS_DARK_THEME_KEY, value)
                .apply()
        }

    var searchHistoryList: String?
        get() = sharedPreferences.getString(SEARCH_HISTORY_LIST_KEY, "")
        set(value) {
            sharedPreferences.edit()
                .putString(SEARCH_HISTORY_LIST_KEY, value)
                .apply()
        }
}