package com.example.playlistmaker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.main_search_button)
        val libraryButton = findViewById<Button>(R.id.main_library_button)
        val settingsButton = findViewById<Button>(R.id.main_settings_button)

        searchButton.setOnClickListener {
            val searchButtonIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchButtonIntent)
        }

        libraryButton.setOnClickListener {
            val libraryButtonIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryButtonIntent)
        }

        settingsButton.setOnClickListener {
            val settingsButtonIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsButtonIntent)
        }
    }
}