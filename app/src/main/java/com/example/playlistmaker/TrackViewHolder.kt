package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackNameView: TextView = itemView.findViewById(R.id.tvTrackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.tvArtistName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.tvTrackTime)
    private val artworkView: ImageView = itemView.findViewById(R.id.ivArtwork)

    fun bind(track: Track) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackTimeView.text = track.trackTime

        Glide.with(itemView)
            .load("https://img.freepik.com/free-vector/open-blue-book-white_1308-69339.jpg")
            .centerInside()
            .into(artworkView)
    }
}