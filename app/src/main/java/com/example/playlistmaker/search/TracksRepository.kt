package com.example.playlistmaker.search

import com.example.playlistmaker.search.domain.model.Track

interface TracksRepository {
    fun searchTracks(term: String): List<Track>
}