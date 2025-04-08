package com.example.playlistmaker

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(
    private val tracks: List<Track>,
    private val searchHistory: SearchHistory
) : RecyclerView.Adapter<TrackViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AudioPlayerActivity::class.java).apply {
                putExtra("TRACK", tracks[position])
            }
            holder.itemView.context.startActivity(intent)

            Handler(Looper.getMainLooper()).postDelayed({
                searchHistory.addToHistory(tracks[position])
            }, 200)
        }
    }

    override fun getItemCount() = tracks.size
}