package com.example.playlistmaker.search.presentation.debounce

interface ClickDebouncer {
    fun isClickAllowed(): Boolean
    fun notifyClickPerformed()
    fun reset()
}