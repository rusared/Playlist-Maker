package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import java.time.format.DateTimeFormatter

class AudioPlayerActivity : AppCompatActivity() {

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
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var playbackProgressHandler = Handler(Looper.getMainLooper())

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

        currentTrack = (intent.getSerializableExtra(TracksAdapter.TRACK) as? Track)!!
        if (currentTrack == null) {
            Toast.makeText(this, "Трек не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        trackNameValue.text = currentTrack.trackName
        artistNameValue.text = currentTrack.artistName
        durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime.toLong())

        if (currentTrack.collectionName.isNullOrEmpty()) {
            album.visibility = GONE
            albumValue.visibility = GONE
        } else {
            albumValue.text = currentTrack.collectionName
            album.visibility = VISIBLE
            albumValue.visibility = VISIBLE
        }

        yearValue.text = LocalDate.parse(currentTrack.releaseDate, DateTimeFormatter.ISO_DATE_TIME).year.toString()
        genreValue.text = currentTrack.primaryGenreName
        countryValue.text = currentTrack.country
        playbackProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)

        val radiusInDp = 8
        val density = resources.displayMetrics.density
        val radiusInPx = (radiusInDp * density).toInt()

        Glide.with(this)
            .load(currentTrack.getCoverArtwork())
            .placeholder(R.drawable.album_placeholder)
            .transform(RoundedCorners(radiusInPx))
            .into(findViewById(R.id.iv_artwork))

        preparePlayer()
        playButton.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        playbackProgressHandler?.removeCallbacksAndMessages(null)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.setImageDrawable(getDrawable(R.drawable.play_button))
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageDrawable(getDrawable(R.drawable.play_button))
            playerState = STATE_PREPARED
            playbackProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)
            playbackProgressHandler?.removeCallbacksAndMessages(null)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageDrawable(getDrawable(R.drawable.pause_button))
        playerState = STATE_PLAYING
        startPlaybackProgress()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageDrawable(getDrawable(R.drawable.play_button))
        playerState = STATE_PAUSED
        playbackProgressHandler?.removeCallbacksAndMessages(null)
    }

    private fun startPlaybackProgress() {
        playbackProgressHandler?.post(
            createUpdateProgressTask()
        )
    }

    private fun createUpdateProgressTask(): Runnable {
        return object : Runnable {
            override fun run() {
                playbackProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                playbackProgressHandler?.postDelayed(this, DELAY)
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }
}