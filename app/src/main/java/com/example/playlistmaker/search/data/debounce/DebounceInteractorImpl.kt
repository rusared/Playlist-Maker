package com.example.playlistmaker.search.data.debounce

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.search.domain.interactor.DebounceInteractor

class DebounceInteractorImpl : DebounceInteractor {
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun debounce(delay: Long, action: () -> Unit) {
        searchRunnable?.let { handler.removeCallbacks(it) }

        searchRunnable = Runnable { action() }
        handler.postDelayed(searchRunnable!!, delay)
    }

    override fun cancel() {
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = null
    }
}