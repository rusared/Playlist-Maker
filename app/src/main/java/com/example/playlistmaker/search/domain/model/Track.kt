package com.example.playlistmaker.search.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    val trackName: String,         // Название композиции
    val artistName: String,        // Имя исполнителя
    @SerializedName("trackTimeMillis")
    val trackTimeMillis: String,         // Продолжительность трека
    val artworkUrl100: String,     // Ссылка на изображение обложки
    val trackId: String,           // Идентификатор трека
    val collectionName: String,    // Название альбома
    val releaseDate: String,       // Год релиза трека
    val primaryGenreName: String,  // Жанр трека
    val country: String,           // Страна исполнителя
    val previewUrl: String         // Ссылка на отрывок трека
) : Parcelable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}