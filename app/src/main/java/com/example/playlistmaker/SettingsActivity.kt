package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<MaterialToolbar>(R.id.settings_back_button)
        setSupportActionBar(backButton)
        val shareButton = findViewById<MaterialTextView>(R.id.settings_share_button)
        val supportButton = findViewById<MaterialTextView>(R.id.settings_support_button)
        val agreementButton = findViewById<MaterialTextView>(R.id.settings_agreement_button)

        backButton.setNavigationOnClickListener {
            finish()
        }
        shareButton.setOnClickListener {
            shareApp()
        }
        supportButton.setOnClickListener {
            mailToSupport()
        }
        agreementButton.setOnClickListener {
            showUserAgreement()
        }
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_url))
        startActivity(Intent.createChooser(intent, resources.getString(R.string.share_via)))
    }

    private fun mailToSupport() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, resources.getString(R.string.support_email))
        intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.support_mail_subject))
        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.support_mail_text))
        startActivity(intent)
    }

    private fun showUserAgreement() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(resources.getString(R.string.user_agreement))
        startActivity(intent)
    }
}