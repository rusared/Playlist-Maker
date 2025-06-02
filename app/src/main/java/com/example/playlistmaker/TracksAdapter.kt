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

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val ADD_HISTORY_DELAY = 500L
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])


        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                val intent =
                    Intent(holder.itemView.context, AudioPlayerActivity::class.java).apply {
                        putExtra("TRACK", tracks[position])
                    }
                holder.itemView.context.startActivity(intent)

                Handler(Looper.getMainLooper()).postDelayed({
                    searchHistory.addToHistory(tracks[position])
                }, ADD_HISTORY_DELAY)
            }

        }
    }

    override fun getItemCount() = tracks.size


    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current

    }
}