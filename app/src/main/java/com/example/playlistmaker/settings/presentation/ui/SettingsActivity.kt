package com.example.playlistmaker.settings.presentation.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var isFromUserInteraction = false

    private val viewModel: SettingsViewModel by viewModels {
        Creator.provideSettingsViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.settingsBackButton)
        binding.settingsBackButton.setNavigationOnClickListener { finish() }

        binding.themeSwitcher.setOnCheckedChangeListener(null)
        binding.themeSwitcher.isChecked = viewModel.observeThemeState.value?.isDarkTheme ?: false

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isFromUserInteraction) {
                viewModel.onThemeChanged(isChecked)
            }
        }

        binding.shareButton.setOnClickListener { viewModel.onShareAppClicked() }
        binding.supportButton.setOnClickListener { viewModel.onSupportClicked() }
        binding.agreementButton.setOnClickListener { viewModel.onAgreementClicked() }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.observeThemeState.observe(this, Observer { themeSettings ->
            isFromUserInteraction = false
            binding.themeSwitcher.isChecked = themeSettings.isDarkTheme
            isFromUserInteraction = true

            applyTheme(themeSettings.isDarkTheme)
        })
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}