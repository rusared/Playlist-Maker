package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.main.presentation.view_model.MainViewModelFactory
import com.example.playlistmaker.settings.data.datasource.AppPreferences
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.settings.data.repository.PreferencesRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.settings.domain.repository.PreferencesRepository
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.player.domain.interactor.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModelFactory
import com.example.playlistmaker.search.data.debounce.DebounceInteractorImpl
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.interactor.DebounceInteractor
import com.example.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.presentation.view_model.SearchViewModelFactory
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModelFactory
import com.example.playlistmaker.sharing.data.repository.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.repository.SharingRepository

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private fun provideDebounceInteractor(): DebounceInteractor {
        return DebounceInteractorImpl()
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(
            getTracksRepository(),
            provideDebounceInteractor())
    }

    fun providePreferencesRepository(context: Context): PreferencesRepository {
        val appPreferences = AppPreferences(context)
        return PreferencesRepositoryImpl(appPreferences)
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val preferencesRepository = providePreferencesRepository(context)
        return SearchHistoryRepositoryImpl(preferencesRepository)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val searchHistoryRepository = provideSearchHistoryRepository(context)
        return SearchHistoryInteractor(searchHistoryRepository)
    }

    // Settings
    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val repository = provideSettingsRepository(context)
        return SettingsInteractorImpl(repository)
    }

    private fun provideSettingsRepository(context: Context): SettingsRepository {
        val appPreferences = AppPreferences(context)
        return SettingsRepositoryImpl(appPreferences)
    }

    // MainViewModel Factory
    fun provideMainViewModelFactory(): MainViewModelFactory {
        return MainViewModelFactory()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerRepository())
    }

    fun providePlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerViewModelFactory(): PlayerViewModelFactory {
        return PlayerViewModelFactory(providePlayerInteractor())
    }

    // Sharing
    fun provideSharingInteractor(context: Context): SharingInteractor {
        val navigator = provideExternalNavigator(context)
        val sharingRepository = provideSharingRepository(context)
        return SharingInteractorImpl(navigator, sharingRepository)
    }

    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    private fun provideSharingRepository(context: Context): SharingRepository {
        return SharingRepositoryImpl(context)
    }

    // SettingsViewModel Factory
    fun provideSettingsViewModelFactory(context: Context): SettingsViewModelFactory {
        val sharingInteractor = provideSharingInteractor(context)
        val settingsInteractor = provideSettingsInteractor(context)
        return SettingsViewModelFactory(sharingInteractor, settingsInteractor)
    }

    // SearchViewModel Factory
    fun provideSearchViewModelFactory(context: Context): SearchViewModelFactory {
        return SearchViewModelFactory(
            provideSearchInteractor(),
            provideSearchHistoryInteractor(context)
        )
    }
}