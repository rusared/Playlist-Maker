package com.example.playlistmaker

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface ITunesSearchAPI {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TracksResponse>
}