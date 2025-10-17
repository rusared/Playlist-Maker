package com.example.playlistmaker.main.domain.interactor

import com.example.playlistmaker.main.domain.model.NavigationEvent

interface MainInteractor {
    fun handleSearchClick(): NavigationEvent
    fun handleLibraryClick(): NavigationEvent
    fun handleSettingsClick(): NavigationEvent
}