package com.example.playlistmaker.search.domain.interactor

import android.os.Handler
import android.os.Looper

class DebounceInteractor {
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    fun debounce(delay: Long, action: () -> Unit) {
        searchRunnable?.let { handler.removeCallbacks(it) }

        searchRunnable = Runnable { action() }
        handler.postDelayed(searchRunnable!!, delay)
    }

    fun cancel() {
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = null
    }
}