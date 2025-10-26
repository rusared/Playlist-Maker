package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.network.ITunesSearchAPI
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.presentation.debounce.ClickDebouncer
import com.example.playlistmaker.search.presentation.debounce.ClickDebouncerImpl
import com.example.playlistmaker.settings.data.datasource.AppPreferences
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesSearchAPI> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesSearchAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            AppPreferences.PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    single {
        AppPreferences(get())
    }

    single<Gson> {
        Gson()
    }

    factory<ClickDebouncer> {
        ClickDebouncerImpl()
    }
}