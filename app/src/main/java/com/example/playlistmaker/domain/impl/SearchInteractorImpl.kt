package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.ui.SearchActivity
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: TracksRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(term)
                val status = if (tracks.isEmpty()) {
                    SearchActivity.RequestStatus.NOTHING_FOUND
                } else {
                    SearchActivity.RequestStatus.SUCCESS
                }
                consumer.consume(tracks, status)
            } catch (e: Exception) {
                consumer.consume(emptyList(), SearchActivity.RequestStatus.CONNECTION_PROBLEM)
            }
        }
    }
}