package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val iTunesBaseUrl = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService: ITunesSearchAPI = retrofit.create(ITunesSearchAPI::class.java)

    override fun doRequest(dto: Any): Response<*> {
        return if (dto is TracksSearchRequest) {
            val response: Response<TracksSearchResponse> = iTunesService.search(dto.term).execute()
            response
        } else {
            Response.error<TracksSearchResponse>(400, okhttp3.ResponseBody.create(null, "Bad Request"))
        }
    }
}