package com.example.playlistmaker.search.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.model.Track

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val state = MutableLiveData<SearchState>()
    val observeState: LiveData<SearchState> get() = state

    private val history = MutableLiveData<List<Track>>()
    val observeHistory: LiveData<List<Track>> get() = history

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable = Runnable { }

    //Детальное логирование


    init {
        loadSearchHistory()
    }

    fun setupSearchRunnable(searchAction: () -> Unit) {
        handler.removeCallbacks(searchRunnable)
        searchRunnable = Runnable { searchAction() }
    }

    fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun searchTracks(query: String) {
        if (query.isEmpty()) return

        state.postValue(SearchState.Loading)

        searchInteractor.searchTracks(query, object : SearchInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>, status: SearchStatus) {
                handler.post {
                    val newState = when (status) {
                        SearchStatus.SUCCESS -> SearchState.Content(foundTracks)
                        SearchStatus.NOTHING_FOUND -> SearchState.Empty
                        SearchStatus.CONNECTION_PROBLEM -> SearchState.Error
                    }
                    state.value = newState
                }
            }
        })
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
        handler.removeCallbacks(searchRunnable)
    }

    sealed class SearchState {
        object Default : SearchState()
        object Loading : SearchState()
        object Empty : SearchState()
        object Error : SearchState()
        data class Content(val tracks: List<Track>) : SearchState()
    }

    enum class SearchStatus {
        SUCCESS,
        NOTHING_FOUND,
        CONNECTION_PROBLEM
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}