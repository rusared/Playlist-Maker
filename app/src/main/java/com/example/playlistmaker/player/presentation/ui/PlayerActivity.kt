package com.example.playlistmaker.player.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.ui.TracksAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PlayerActivity : AppCompatActivity() {

    private val viewModel: PlayerViewModel by viewModels {
        Creator.providePlayerViewModelFactory()
    }

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
    private lateinit var playbackProgress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        initViews()
        setupClickListeners()
        observeViewModel()
        setupTrack()
    }

    private fun initViews() {
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
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            viewModel.playPause()
        }
    }

    private fun observeViewModel() {
        viewModel.observePlayerState.observe(this) { state ->
            if (state == PlayerInteractor.PlayerState.COMPLETED) {
                updatePlayButtonForCompleted()
            }
        }

        viewModel.observeIsPlaying.observe(this) { isPlaying ->
            updatePlayButton(isPlaying)
        }

        viewModel.observePlaybackProgress.observe(this) { formattedTime ->
            playbackProgress.text = formattedTime
        }

        viewModel.observeTrackDuration.observe(this) { duration ->
            durationValue.text = duration
        }
    }

    private fun setupTrack() {
        val track = intent.getParcelableExtra<Track>(TracksAdapter.Companion.TRACK) ?: run {
            Toast.makeText(this, getString(R.string.track_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.setTrack(track)
        setupTrackInfo(track)
        viewModel.preparePlayer()
    }

    private fun setupTrackInfo(track: Track) {
        trackNameValue.text = track.trackName
        artistNameValue.text = track.artistName

        if (track.collectionName.isNullOrEmpty()) {
            album.visibility = View.GONE
            albumValue.visibility = View.GONE
        } else {
            albumValue.text = track.collectionName
            album.visibility = View.VISIBLE
            albumValue.visibility = View.VISIBLE
        }

        yearValue.text = LocalDate.parse(track.releaseDate, DateTimeFormatter.ISO_DATE_TIME).year.toString()
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country
        playbackProgress.text = getString(R.string.default_progress)

        val radiusInDp = 8
        val density = resources.displayMetrics.density
        val radiusInPx = (radiusInDp * density).toInt()

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.album_placeholder)
            .transform(RoundedCorners(radiusInPx))
            .into(findViewById(R.id.iv_artwork))
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        playButton.setImageResource(
            if (isPlaying) R.drawable.pause_button else R.drawable.play_button
        )
    }

    private fun updatePlayButtonForCompleted() {
        playButton.setImageResource(R.drawable.play_button)
    }
}