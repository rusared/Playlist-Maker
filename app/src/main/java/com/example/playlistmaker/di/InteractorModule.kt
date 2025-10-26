package com.example.playlistmaker.di

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.interactor.PlayerInteractorImpl
import com.example.playlistmaker.search.data.debounce.DebounceInteractorImpl
import com.example.playlistmaker.search.domain.interactor.DebounceInteractor
import com.example.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    factory<DebounceInteractor> {
        DebounceInteractorImpl(get())
    }

    factory<Handler> {
        Handler(Looper.getMainLooper())
    }

    single<SearchInteractor> {
        SearchInteractorImpl(get(), get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get(), get(), get())
    }

}