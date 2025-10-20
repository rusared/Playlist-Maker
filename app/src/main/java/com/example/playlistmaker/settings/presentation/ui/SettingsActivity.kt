package com.example.playlistmaker.settings.presentation.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeSwitcher: SwitchMaterial
    private var isFromUserInteraction = false

    private val viewModel: SettingsViewModel by viewModels {
        Creator.provideSettingsViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        setupObservers()
    }

    private fun initViews() {
        val backButton = findViewById<MaterialToolbar>(R.id.mt_back_button)
        themeSwitcher = findViewById(R.id.sm_theme_switcher)
        val shareButton = findViewById<MaterialTextView>(R.id.mtv_share_button)
        val supportButton = findViewById<MaterialTextView>(R.id.mtv_support_button)
        val agreementButton = findViewById<MaterialTextView>(R.id.mtv_agreement_button)

        setSupportActionBar(backButton)
        backButton.setNavigationOnClickListener { finish() }

        themeSwitcher.setOnCheckedChangeListener(null)
        themeSwitcher.isChecked = viewModel.observeThemeState.value?.isDarkTheme ?: false

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isFromUserInteraction) {
                viewModel.onThemeChanged(isChecked)
            }
        }

        shareButton.setOnClickListener { viewModel.onShareAppClicked() }
        supportButton.setOnClickListener { viewModel.onSupportClicked() }
        agreementButton.setOnClickListener { viewModel.onAgreementClicked() }
    }

    private fun setupObservers() {
        viewModel.observeThemeState.observe(this, Observer { themeSettings ->
            isFromUserInteraction = false
            themeSwitcher.isChecked = themeSettings.isDarkTheme
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