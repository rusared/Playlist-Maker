package com.example.playlistmaker.search.presentation.debounce

import android.os.Handler
import android.os.Looper

class ClickDebouncerImpl : ClickDebouncer {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun isClickAllowed(): Boolean {
        val currentState = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({
                isClickAllowed = true
            }, CLICK_DEBOUNCE_DELAY)
        }

        return currentState
    }

    override fun notifyClickPerformed() {
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({
                isClickAllowed = true
            }, CLICK_DEBOUNCE_DELAY)
        }
    }

    override fun reset() {
        handler.removeCallbacksAndMessages(null)
        isClickAllowed = true
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}