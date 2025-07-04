package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(term: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(term))
        if (response.resultCode == 200) {
            val tracksResponse = response as? TracksSearchResponse
            return tracksResponse?.results?.map { dto ->
                Track(
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    trackTimeMillis = SimpleDateFormat("mm:ss", Locale.getDefault()).format(dto.trackTimeMillis),
                    artworkUrl100 = dto.artworkUrl100,
                    trackId = dto.trackId.toString(),
                    collectionName = dto.collectionName.toString(),
                    releaseDate = dto.releaseDate,
                    primaryGenreName = dto.primaryGenreName,
                    country = dto.country,
                    previewUrl = dto.previewUrl.toString()
                )
            } ?: emptyList()
        }
        return emptyList()
    }
}