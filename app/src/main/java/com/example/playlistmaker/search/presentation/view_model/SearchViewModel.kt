package com.example.playlistmaker.search.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.interactor.SearchInteractor.SearchStatus
import com.example.playlistmaker.search.domain.model.Track

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val state = MutableLiveData<SearchState>()
    val observeState: LiveData<SearchState> get() = state

    private val history = MutableLiveData<List<Track>>()
    val observeHistory: LiveData<List<Track>> get() = history

    private val searchConsumer = object : SearchInteractor.TracksConsumer {
        override fun consume(foundTracks: List<Track>, status: SearchStatus) {
            val newState = when (status) {
                SearchStatus.SUCCESS -> SearchState.Content(foundTracks)
                SearchStatus.NOTHING_FOUND -> SearchState.Empty
                SearchStatus.CONNECTION_PROBLEM -> SearchState.Error
            }
            state.postValue(newState)
        }
    }

    init {
        loadSearchHistory()
    }

    fun searchDebounce(query: String) {
        if (query.isEmpty()) return
        searchInteractor.setupDebounce(query, searchConsumer)
    }

    fun searchTracksImmediately(query: String) {
        if (query.isEmpty()) return
        state.postValue(SearchState.Loading)
        searchInteractor.searchTracks(query, searchConsumer)
    }

    fun cancelSearch() {
        searchInteractor.cancelDebounce()
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addToHistory(track)
        loadSearchHistory()
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
        loadSearchHistory()
    }

    fun loadSearchHistory() {
        history.value = searchHistoryInteractor.getHistory()
    }

    fun clearSearch() {
        state.value = SearchState.Default
    }

    override fun onCleared() {
        super.onCleared()
        searchInteractor.cancelDebounce()
    }

    sealed interface SearchState {
        object Default : SearchState
        object Loading : SearchState
        object Empty : SearchState
        object Error : SearchState
        data class Content(val tracks: List<Track>) : SearchState
    }
}