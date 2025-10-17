package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track

interface SearchInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)
    fun setupDebounce(term: String, consumer: TracksConsumer)
    fun cancelDebounce()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>, status: SearchStatus)
    }

    enum class SearchStatus {
        SUCCESS,
        NOTHING_FOUND,
        CONNECTION_PROBLEM
    }
}