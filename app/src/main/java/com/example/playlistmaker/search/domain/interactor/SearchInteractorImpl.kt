package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel.SearchStatus
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val repository: TracksRepository,
    private val debounceInteractor: DebounceInteractor = DebounceInteractor()
) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(term)
                val status = if (tracks.isEmpty()) {
                    SearchStatus.NOTHING_FOUND
                } else {
                    SearchStatus.SUCCESS
                }
                consumer.consume(tracks, status)
            } catch (e: Exception) {
                consumer.consume(emptyList(), SearchStatus.CONNECTION_PROBLEM)
            }
        }
    }

    override fun setupDebounce(term: String, consumer: SearchInteractor.TracksConsumer) {
        debounceInteractor.debounce(SEARCH_DEBOUNCE_DELAY) {
            searchTracks(term, consumer)
        }
    }

    override fun cancelDebounce() {
        debounceInteractor.cancel()
    }

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}