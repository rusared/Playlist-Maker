package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.settings.data.repository.PreferencesRepositoryImpl
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.PreferencesRepository
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.sharing.data.repository.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.repository.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<PreferencesRepository> {
        PreferencesRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }

}