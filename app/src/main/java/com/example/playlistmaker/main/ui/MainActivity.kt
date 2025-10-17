package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.presentation.ui.SearchActivity
import com.example.playlistmaker.settings.presentation.ui.SettingsActivity
import com.example.playlistmaker.library.ui.LibraryActivity
import com.example.playlistmaker.main.domain.model.NavigationEvent
import com.example.playlistmaker.main.ui.view_model.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        Creator.provideMainViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.main_search_button)
        val libraryButton = findViewById<Button>(R.id.main_library_button)
        val settingsButton = findViewById<Button>(R.id.main_settings_button)

        searchButton.setOnClickListener {
            viewModel.onSearchClicked()
        }

        libraryButton.setOnClickListener {
            viewModel.onLibraryClicked()
        }

        settingsButton.setOnClickListener {
            viewModel.onSettingsClicked()
        }

        observeNavigationEvents()
    }

    private fun observeNavigationEvents() {
        viewModel.observeNavigationEvent.observe(this) { event ->
            when (event) {
                is NavigationEvent.Search -> navigateToSearch()
                is NavigationEvent.Library -> navigateToLibrary()
                is NavigationEvent.Settings -> navigateToSettings()
            }
        }
    }

    private fun navigateToSearch() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToLibrary() {
        val intent = Intent(this, LibraryActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}