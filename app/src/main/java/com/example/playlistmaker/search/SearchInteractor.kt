package com.example.playlistmaker.search

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.ui.SearchActivity

interface SearchInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>, status: SearchActivity.RequestStatus)
    }
}