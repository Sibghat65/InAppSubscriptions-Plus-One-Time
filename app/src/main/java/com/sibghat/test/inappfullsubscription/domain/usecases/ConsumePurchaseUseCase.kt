package com.sibghat.test.inappfullsubscription.domain.usecases

import com.sibghat.test.inappfullsubscription.data.remote.repositories.InAppBillingRepository
import kotlinx.coroutines.flow.Flow

/**
 * ⚠️ WARNING: This UseCase should ONLY be used in DEBUG builds for testing purposes.
 *
 * This class is responsible for consuming **one-time purchases (INAPP)** to allow
 * re-purchasing the same product during development or QA testing.
 *
 * Do NOT use this in production builds, as consumption of purchases should only happen
 * once after successful fulfillment of the product.
 *
 * Usage:
 * - Call this use case to manually consume a one-time purchase token in debug mode.
 * - This helps simulate re-purchasing behavior for testing billing flows.
 */
class ConsumePurchaseUseCase(private val inAppRepository: InAppBillingRepository) {
    suspend operator fun invoke(purchaseToken: String): Flow<Boolean> {
        return inAppRepository.consumePurchase(purchaseToken)
    }
}

