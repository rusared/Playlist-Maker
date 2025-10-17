package com.example.playlistmaker.search.domain.interactor

interface DebounceInteractor {
    fun debounce(delay: Long, action: () -> Unit)
    fun cancel()
}