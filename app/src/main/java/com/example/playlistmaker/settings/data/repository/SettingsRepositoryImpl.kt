package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.settings.data.datasource.AppPreferences
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val appPreferences: AppPreferences
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            isThemeSet = appPreferences.isThemeSet,
            isDarkTheme = appPreferences.isDarkTheme
        )
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        appPreferences.isThemeSet = settings.isThemeSet
        appPreferences.isDarkTheme = settings.isDarkTheme
    }
}