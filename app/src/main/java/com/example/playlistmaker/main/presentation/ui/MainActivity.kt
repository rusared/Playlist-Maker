package com.example.playlistmaker.main.presentation.ui

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
import com.example.playlistmaker.main.presentation.view_model.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var searchButton: Button
    private lateinit var libraryButton: Button
    private lateinit var settingsButton: Button

    private val viewModel: MainViewModel by viewModels {
        Creator.provideMainViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()
        observeNavigationEvents()
    }

    private fun initViews() {
        searchButton = findViewById(R.id.main_search_button)
        libraryButton = findViewById(R.id.main_library_button)
        settingsButton = findViewById(R.id.main_settings_button)
    }

    private fun setupClickListeners() {

        searchButton.setOnClickListener {
            viewModel.onSearchClicked()
        }

        libraryButton.setOnClickListener {
            viewModel.onLibraryClicked()
        }

        settingsButton.setOnClickListener {
            viewModel.onSettingsClicked()
        }
    }

    private fun observeNavigationEvents() {
        viewModel.observeSearchClicked.observe(this) {
            navigateToSearch()
        }

        viewModel.observeLibraryClicked.observe(this) {
            navigateToLibrary()
        }

        viewModel.observeSettingsClicked.observe(this) {
            navigateToSettings()
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