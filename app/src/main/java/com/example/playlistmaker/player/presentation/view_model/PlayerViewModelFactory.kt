package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor

class PlayerViewModelFactory(
    private val playerInteractor: PlayerInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(playerInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}