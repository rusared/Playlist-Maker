package com.example.playlistmaker

import com.example.playlistmaker.data.PreferencesManager
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchInteractorImpl

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getTracksRepository())
    }

    fun provideHistoryInteractor(preferencesManager: PreferencesManager): HistoryInteractor {
        return HistoryInteractorImpl(SearchHistory().apply {
            this.preferencesManager = preferencesManager
        }, preferencesManager)
    }

    fun providePlayerInteractor(): PlayerInteractorImpl {
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }
}