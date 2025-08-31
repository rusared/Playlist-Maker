package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.AppPreferences
import com.example.playlistmaker.domain.impl.SearchHistoryInteractor
import com.example.playlistmaker.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.impl.PreferencesRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PreferencesRepository
import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractor

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getTracksRepository())
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }

    fun providePreferencesRepository(context: Context): PreferencesRepository {
        val appPreferences = AppPreferences(context)
        return PreferencesRepositoryImpl(appPreferences)
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        val repository = providePreferencesRepository(context)
        return ThemeInteractor(repository)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val repository = providePreferencesRepository(context)
        return SearchHistoryInteractor(repository)
    }
}