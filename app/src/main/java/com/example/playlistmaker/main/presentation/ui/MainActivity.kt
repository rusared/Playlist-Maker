package com.example.playlistmaker.main.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.search.presentation.ui.SearchActivity
import com.example.playlistmaker.settings.presentation.ui.SettingsActivity
import com.example.playlistmaker.library.ui.LibraryActivity
import com.example.playlistmaker.main.presentation.view_model.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        Creator.provideMainViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeNavigationEvents()
    }

    private fun setupClickListeners() {

        binding.mainSearchButton.setOnClickListener {
            viewModel.onSearchClicked()
        }

        binding.mainLibraryButton.setOnClickListener {
            viewModel.onLibraryClicked()
        }

        binding.mainSettingsButton.setOnClickListener {
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