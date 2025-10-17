package com.example.playlistmaker.search.presentation.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.interactor.ClickDebouncer
import com.example.playlistmaker.search.domain.model.Track

class TracksAdapter(
    private var tracks: List<Track>,
    private val clickListener: (Track) -> Unit,
    private val clickDebouncer: ClickDebouncer
) : RecyclerView.Adapter<TrackViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            if (clickDebouncer.isClickAllowed()) {
                clickListener(tracks[position])
            }
        }
    }

    override fun getItemCount() = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        this.tracks = newTracks
        notifyDataSetChanged()
    }

    fun onDestroy() {
        clickDebouncer.reset()
    }

    companion object {
        const val TRACK = "TRACK"
    }
}