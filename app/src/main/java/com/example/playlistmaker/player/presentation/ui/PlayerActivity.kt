package com.example.playlistmaker.player.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
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

    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
        setupTrack()
    }

    private fun setupClickListeners() {
        binding.playerBackButton.setOnClickListener {
            finish()
        }

        binding.playButton.setOnClickListener {
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
            binding.playbackProgress.text = formattedTime
        }

        viewModel.observeTrackDuration.observe(this) { duration ->
            binding.durationValue.text = duration
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
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName

        if (track.collectionName.isNullOrEmpty()) {
            binding.album.visibility = View.GONE
            binding.albumValue.visibility = View.GONE
        } else {
            binding.albumValue.text = track.collectionName
            binding.album.visibility = View.VISIBLE
            binding.albumValue.visibility = View.VISIBLE
        }

        binding.yearValue.text = LocalDate.parse(track.releaseDate, DateTimeFormatter.ISO_DATE_TIME).year.toString()
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        binding.playbackProgress.text = getString(R.string.default_progress)

        val radiusInDp = 8
        val density = resources.displayMetrics.density
        val radiusInPx = (radiusInDp * density).toInt()

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.album_placeholder)
            .transform(RoundedCorners(radiusInPx))
            .into(binding.artwork)
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        binding.playButton.setImageResource(
            if (isPlaying) R.drawable.pause_button else R.drawable.play_button
        )
    }

    private fun updatePlayButtonForCompleted() {
        binding.playButton.setImageResource(R.drawable.play_button)
    }
}