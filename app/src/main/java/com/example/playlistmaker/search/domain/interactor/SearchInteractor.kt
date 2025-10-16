package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel.SearchStatus

interface SearchInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)
    fun setupDebounce(term: String, consumer: TracksConsumer)
    fun cancelDebounce()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>, status: SearchStatus)
    }
}