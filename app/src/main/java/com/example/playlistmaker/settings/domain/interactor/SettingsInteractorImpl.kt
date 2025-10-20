package com.example.playlistmaker.settings.domain.interactor

import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.updateThemeSetting(settings)
    }
}