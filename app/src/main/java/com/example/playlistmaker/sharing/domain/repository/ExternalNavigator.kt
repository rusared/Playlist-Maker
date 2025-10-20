package com.example.playlistmaker.sharing.domain.repository

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(url: String)
    fun openEmail(emailData: EmailData)
}