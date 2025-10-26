package com.example.playlistmaker.main.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.search.presentation.ui.SearchActivity
import com.example.playlistmaker.settings.presentation.ui.SettingsActivity
import com.example.playlistmaker.library.ui.LibraryActivity
import com.example.playlistmaker.main.presentation.view_model.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val vm by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeNavigationEvents()
    }

    private fun setupClickListeners() {

        binding.mainSearchButton.setOnClickListener {
            vm.onSearchClicked()
        }

        binding.mainLibraryButton.setOnClickListener {
            vm.onLibraryClicked()
        }

        binding.mainSettingsButton.setOnClickListener {
            vm.onSettingsClicked()
        }
    }

    private fun observeNavigationEvents() {
        vm.observeSearchClicked.observe(this) {
            navigateToSearch()
        }

        vm.observeLibraryClicked.observe(this) {
            navigateToLibrary()
        }

        vm.observeSettingsClicked.observe(this) {
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