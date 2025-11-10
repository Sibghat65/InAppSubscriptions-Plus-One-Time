package com.sibghat.test.inappfullsubscription.domain.usecases

import com.sibghat.test.inappfullsubscription.data.remote.repositories.InAppBillingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Establishes a connection to the Google Play Billing service.
 *
 * This UseCase is responsible for initializing and maintaining the billing connection
 * through the [InAppBillingRepository]. It should typically be called once during app startup
 * or before performing any billing-related operations (e.g., querying products or purchases).
 *
 * ⚙️ Usage:
 * - Call this before invoking any billing-related UseCases.
 * - It returns a [Flow<Boolean>] indicating the connection state.
 *
 * ⚠️ Note:
 * This UseCase does not handle product queries or purchases — it only manages
 * the connection lifecycle with the billing client.
 */
class ConnectBillingUseCase(private val inAppRepository: InAppBillingRepository) {
    suspend operator fun invoke(): Flow<Boolean> {
        return inAppRepository.connectFlow()
    }
}
