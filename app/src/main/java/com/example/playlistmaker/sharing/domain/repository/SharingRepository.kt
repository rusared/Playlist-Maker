package com.example.playlistmaker.sharing.domain.repository

import com.example.playlistmaker.sharing.domain.model.EmailData

interface SharingRepository {
    fun getShareAppLink(): String
    fun getSupportEmailData(): EmailData
    fun getTermsLink(): String
}