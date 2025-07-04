package com.example.playlistmaker.presentation

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.PreferencesManager

class App : Application() {

    var darkTheme = false
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)

        if (!preferencesManager.isThemeSet) {
            val isSystemDarkTheme = (resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES
            preferencesManager.isDarkTheme = isSystemDarkTheme
            preferencesManager.isThemeSet = true
        }

        darkTheme = preferencesManager.isDarkTheme
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        preferencesManager.isDarkTheme = darkThemeEnabled
    }
}