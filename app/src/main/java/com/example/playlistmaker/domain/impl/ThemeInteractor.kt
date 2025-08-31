package com.example.playlistmaker.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.PreferencesRepository

class ThemeInteractor(
    private val settingsRepository: PreferencesRepository
) {
    fun getCurrentTheme(): Boolean {
        return settingsRepository.getThemeSettings().isDarkTheme
    }

    fun isThemeSet(): Boolean {
        return settingsRepository.getThemeSettings().isThemeSet
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        val currentSettings = settingsRepository.getThemeSettings()
        settingsRepository.saveThemeSettings(
            currentSettings.copy(
                isThemeSet = true,
                isDarkTheme = enabled
            )
        )
    }

    fun toggleTheme(): Boolean {
        val currentSettings = settingsRepository.getThemeSettings()
        val newDarkTheme = !currentSettings.isDarkTheme
        settingsRepository.saveThemeSettings(
            currentSettings.copy(
                isThemeSet = true,
                isDarkTheme = newDarkTheme
            )
        )
        return newDarkTheme
    }

    fun getThemeMode(): Int {
        return if (isThemeSet()) {
            if (getCurrentTheme()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        } else {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }
}