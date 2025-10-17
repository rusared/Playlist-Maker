package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track

interface TracksRepository {
    fun searchTracks(term: String): List<Track>
}