package com.example.playlistmaker.data

import retrofit2.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response<*>
}