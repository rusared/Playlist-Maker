package com.example.playlistmaker.main.domain.model

sealed class NavigationEvent {
    object Search : NavigationEvent()
    object Library : NavigationEvent()
    object Settings : NavigationEvent()
}