package com.example.playlistmaker

import android.os.Bundle
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

        backButton.setOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra("TRACK") as? Track
        if (track == null) {
            Toast.makeText(this, "Трек не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        trackNameValue.text = track.trackName
        artistNameValue.text = track.artistName
        durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())

        if (track.collectionName.isNullOrEmpty()) {
            album.visibility = GONE
            albumValue.visibility = GONE
        } else {
            albumValue.text = track.collectionName
            album.visibility = VISIBLE
            albumValue.visibility = VISIBLE
        }

        yearValue.text = LocalDate.parse(track.releaseDate, DateTimeFormatter.ISO_DATE_TIME).year.toString()
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.album_placeholder)
            .transform(RoundedCorners(8))
            .into(findViewById(R.id.iv_artwork))
    }
}