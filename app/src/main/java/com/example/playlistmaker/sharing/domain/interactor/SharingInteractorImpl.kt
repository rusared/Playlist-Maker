package com.example.playlistmaker.sharing.domain.interactor

import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.repository.SharingRepository

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val sharingRepository: SharingRepository
) : SharingInteractor {

    override fun shareApp() {
        val shareLink = sharingRepository.getShareAppLink()
        externalNavigator.shareLink(shareLink)
    }

    override fun openTerms() {
        val termsLink = sharingRepository.getTermsLink()
        externalNavigator.openLink(termsLink)
    }

    override fun openSupport() {
        val emailData = sharingRepository.getSupportEmailData()
        externalNavigator.openEmail(emailData)
    }
}