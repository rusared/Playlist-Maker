package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.network.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.network.dto.TracksSearchResponse
import okhttp3.ResponseBody
import retrofit2.Response

class RetrofitNetworkClient(
    private val iTunesService: ITunesSearchAPI
) : NetworkClient {

    override fun doRequest(dto: Any): Response<*> {
        return if (dto is TracksSearchRequest) {
            val response: Response<TracksSearchResponse> = iTunesService.search(dto.term).execute()
            response
        } else {
            Response.error<TracksSearchResponse>(400, ResponseBody.create(null, "Bad Request"))
        }
    }
}