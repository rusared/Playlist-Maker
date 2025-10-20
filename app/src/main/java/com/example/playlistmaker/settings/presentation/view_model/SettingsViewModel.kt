package com.example.playlistmaker.settings.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val themeState = MutableLiveData<ThemeSettings>()
    val observeThemeState: LiveData<ThemeSettings> = themeState

    init {
        loadCurrentTheme()
    }

    private fun loadCurrentTheme() {
        themeState.value = settingsInteractor.getThemeSettings()
    }

    fun onThemeChanged(isDarkTheme: Boolean) {
        val currentSettings = settingsInteractor.getThemeSettings()
        val newSettings = currentSettings.copy(
            isThemeSet = true,
            isDarkTheme = isDarkTheme
        )
        settingsInteractor.updateThemeSetting(newSettings)
        themeState.value = newSettings
    }

    fun onShareAppClicked() {
        sharingInteractor.shareApp()
    }

    fun onSupportClicked() {
        sharingInteractor.openSupport()
    }

    fun onAgreementClicked() {
        sharingInteractor.openTerms()
    }
}