package com.example.playlistmaker.main.domain.interactor

import com.example.playlistmaker.main.domain.model.NavigationEvent

class MainInteractorImpl : MainInteractor {

    override fun handleSearchClick(): NavigationEvent {
        return NavigationEvent.Search
    }

    override fun handleLibraryClick(): NavigationEvent {
        return NavigationEvent.Library
    }

    override fun handleSettingsClick(): NavigationEvent {
        return NavigationEvent.Settings
    }
}