package com.sibghat.test.inappfullsubscription.domain.usecases

import com.sibghat.test.inappfullsubscription.data.remote.repositories.InAppBillingRepository


class TerminateConnectionUseCase(
    private val inAppRepository: InAppBillingRepository
) {
    suspend operator fun invoke() {
        return inAppRepository.endConnection()
    }
}

