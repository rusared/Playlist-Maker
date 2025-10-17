package com.example.playlistmaker.main.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.main.presentation.utils.SingleLiveEvent

class MainViewModel : ViewModel() {

    private val searchClicked = SingleLiveEvent<Unit>()
    val observeSearchClicked: LiveData<Unit> get() = searchClicked

    private val libraryClicked = SingleLiveEvent<Unit>()
    val observeLibraryClicked: LiveData<Unit> get() = libraryClicked

    private val settingsClicked = SingleLiveEvent<Unit>()
    val observeSettingsClicked: LiveData<Unit> get() = settingsClicked

    fun onSearchClicked() {
        searchClicked.call()
    }

    fun onLibraryClicked() {
        libraryClicked.call()
    }

    fun onSettingsClicked() {
        settingsClicked.call()
    }
}