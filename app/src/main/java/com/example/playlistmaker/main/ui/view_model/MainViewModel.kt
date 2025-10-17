package com.example.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.main.domain.model.NavigationEvent

class MainViewModel : ViewModel() {

    private val navigationEvent = MutableLiveData<NavigationEvent>()
    val observeNavigationEvent: LiveData<NavigationEvent> = navigationEvent

    fun onSearchClicked() {
        navigationEvent.value = NavigationEvent.Search
    }

    fun onLibraryClicked() {
        navigationEvent.value = NavigationEvent.Library
    }

    fun onSettingsClicked() {
        navigationEvent.value = NavigationEvent.Settings
    }
}