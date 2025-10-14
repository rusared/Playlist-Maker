package com.example.playlistmaker.search.data.network

import retrofit2.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response<*>
}