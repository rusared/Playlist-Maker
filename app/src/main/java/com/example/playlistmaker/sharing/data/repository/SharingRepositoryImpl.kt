package com.example.playlistmaker.sharing.data.repository

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.repository.SharingRepository

class SharingRepositoryImpl(
    private val context: Context
) : SharingRepository {

    override fun getShareAppLink(): String {
        return context.getString(R.string.app_url)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_mail_subject),
            body = context.getString(R.string.support_mail_text)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.user_agreement)
    }
}