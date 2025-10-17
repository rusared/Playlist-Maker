package com.example.playlistmaker.search.domain.interactor

interface ClickDebouncer {
    fun isClickAllowed(): Boolean
    fun notifyClickPerformed()
    fun reset()
}