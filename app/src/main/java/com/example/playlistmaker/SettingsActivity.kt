package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<MaterialToolbar>(R.id.mt_back_button)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.sm_theme_switcher)
        val shareButton = findViewById<MaterialTextView>(R.id.mtv_share_button)
        val supportButton = findViewById<MaterialTextView>(R.id.mtv_support_button)
        val agreementButton = findViewById<MaterialTextView>(R.id.mtv_agreement_button)

        setSupportActionBar(backButton)

        backButton.setNavigationOnClickListener {
            finish()
        }

        updateThemeSwitcher(themeSwitcher)

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as? App)?.switchTheme(checked)
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

    private fun updateThemeSwitcher(themeSwitcher: SwitchMaterial) {
        themeSwitcher.isChecked = (application as? App)?.darkTheme ?: false
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_url))
        startActivity(Intent.createChooser(intent, resources.getString(R.string.share_via)))
    }

    private fun mailToSupport() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.support_email)))
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