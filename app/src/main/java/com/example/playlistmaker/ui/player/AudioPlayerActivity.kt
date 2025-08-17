package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.tracks.TracksAdapter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity(), PlayerInteractorImpl.PlayerStateListener {

    private lateinit var playerInteractor: PlayerInteractorImpl
    private lateinit var backButton: ImageButton
    private lateinit var trackNameValue: TextView
    private lateinit var artistNameValue: TextView
    private lateinit var durationValue: TextView
    private lateinit var album: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var playButton: ImageButton
    private lateinit var currentTrack: Track
    private lateinit var playbackProgress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        backButton = findViewById(R.id.ib_back_button)
        trackNameValue = findViewById(R.id.tv_track_name)
        artistNameValue = findViewById(R.id.tv_artist_name)
        durationValue = findViewById(R.id.tv_duration_value)
        album = findViewById(R.id.tv_album)
        albumValue = findViewById(R.id.tv_album_value)
        yearValue = findViewById(R.id.tv_year_value)
        genreValue = findViewById(R.id.tv_genre_value)
        countryValue = findViewById(R.id.tv_country_value)
        playButton = findViewById(R.id.ib_play_button)
        playbackProgress = findViewById(R.id.tv_playback_progress)

        backButton.setOnClickListener {
            finish()
        }

        playerInteractor = Creator.providePlayerInteractor()
        currentTrack = intent.getParcelableExtra<Track>(TracksAdapter.TRACK)!!
        if (currentTrack == null) {
            Toast.makeText(this, "Трек не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupTrackInfo(currentTrack)
        setupPlayerControls()
    }

    private fun setupTrackInfo(track: Track) {
        trackNameValue.text = track.trackName
        artistNameValue.text = track.artistName
        durationValue.text = track.trackTimeMillis

        if (track.collectionName.isNullOrEmpty()) {
            album.visibility = View.GONE
            albumValue.visibility = View.GONE
        } else {
            albumValue.text = currentTrack.collectionName
            album.visibility = View.VISIBLE
            albumValue.visibility = View.VISIBLE
        }

        yearValue.text = LocalDate.parse(track.releaseDate, DateTimeFormatter.ISO_DATE_TIME).year.toString()
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country
        playbackProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)

        val radiusInDp = 8
        val density = resources.displayMetrics.density
        val radiusInPx = (radiusInDp * density).toInt()

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.album_placeholder)
            .transform(RoundedCorners(radiusInPx))
            .into(findViewById(R.id.iv_artwork))
    }

    private fun setupPlayerControls() {
        playButton.setOnClickListener {
            playerInteractor.playPause()
        }

        playerInteractor.prepare(currentTrack.previewUrl, this)
    }

    override fun onStateChanged(state: PlayerInteractorImpl.PlayerState) {
        when(state) {
            PlayerInteractorImpl.PlayerState.PREPARED ->
                playButton.setImageResource(R.drawable.play_button)
            PlayerInteractorImpl.PlayerState.PLAYING ->
                playButton.setImageResource(R.drawable.pause_button)
            PlayerInteractorImpl.PlayerState.PAUSED ->
                playButton.setImageResource(R.drawable.play_button)
            PlayerInteractorImpl.PlayerState.COMPLETED -> {
                playButton.setImageResource(R.drawable.play_button)
                playbackProgress.text = formatTime(0)
            }
        }
    }

    override fun onProgressUpdated(position: Int) {
        playbackProgress.text = formatTime(position)
    }

    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.release()
    }
}