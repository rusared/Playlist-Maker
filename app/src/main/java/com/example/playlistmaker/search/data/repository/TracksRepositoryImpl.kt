package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.network.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(term: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(term))
        return if (response.isSuccessful) {
            val tracksResponse = response.body() as? TracksSearchResponse
            val tracks = tracksResponse?.results?.map { dto ->
                Track(
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    trackTimeMillis = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(dto.trackTimeMillis),
                    artworkUrl100 = dto.artworkUrl100,
                    trackId = dto.trackId.toString(),
                    collectionName = dto.collectionName.toString(),
                    releaseDate = dto.releaseDate,
                    primaryGenreName = dto.primaryGenreName,
                    country = dto.country,
                    previewUrl = dto.previewUrl.toString()
                )
            } ?: emptyList()
            tracks

        } else {
            emptyList()
        }
    }
}