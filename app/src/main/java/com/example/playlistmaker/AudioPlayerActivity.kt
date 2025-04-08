package com.example.playlistmaker

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        backButton = findViewById(R.id.ib_back_button)
        backButton.setOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra("TRACK") as? Track
        if (track == null) {
            Toast.makeText(this, "Трек не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.tv_track_name).text = track.trackName
        findViewById<TextView>(R.id.tv_artist_name).text = track.artistName
        findViewById<TextView>(R.id.tv_duration_value).text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        findViewById<TextView>(R.id.tv_album_value).text = track.collectionName
        findViewById<TextView>(R.id.tv_year_value).text = LocalDate.parse(track.releaseDate, DateTimeFormatter.ISO_DATE_TIME).year.toString()
        findViewById<TextView>(R.id.tv_genre_value).text = track.primaryGenreName
        findViewById<TextView>(R.id.tv_country_value).text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.album_placeholder)
            .transform(RoundedCorners(8))
            .into(findViewById(R.id.iv_artwork))
    }
}