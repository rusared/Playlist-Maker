package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup): TrackViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
            return TrackViewHolder(view)
        }
    }

    private val trackNameView: TextView = itemView.findViewById(R.id.tv_track_name)
    private val artistNameView: TextView = itemView.findViewById(R.id.tv_artist_name)
    private val trackTimeView: TextView = itemView.findViewById(R.id.tv_track_time)
    private val artworkView: ImageView = itemView.findViewById(R.id.iv_artwork)

    fun bind(track: Track) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackTimeView.text = track.trackTime

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.album_placeholder)
            .fitCenter()
            .transform(RoundedCorners(2))
            .into(artworkView)
    }
}