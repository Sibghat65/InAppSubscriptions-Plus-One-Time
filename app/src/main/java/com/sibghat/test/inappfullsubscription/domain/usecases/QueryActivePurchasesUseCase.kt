package com.sibghat.test.inappfullsubscription.domain.usecases

import com.android.billingclient.api.BillingClient
import com.sibghat.test.inappfullsubscription.data.remote.repositories.InAppBillingRepository
import kotlinx.coroutines.flow.Flow
/**
 * Queries the user's active purchases from Google Play.
 *
 * This UseCase retrieves currently owned purchases (both one-time and subscriptions)
 * through the [InAppBillingRepository]. It helps verify which products the user
 * has already purchased and are still active.
 *
 * ⚙️ Usage:
 * - Call this UseCase to check if specific products (by [productIds]) are currently owned.
 * - Supports both one-time purchases and subscriptions via the [type] parameter.
 *
 * ⚠️ Note:
 * - Ensure the billing connection is established before calling this UseCase (see [ConnectBillingUseCase]).
 * - The returned [Flow<Boolean>] emits `true` if any of the provided [productIds] are active,
 *   or `false` if none are found.
 */
class QueryActivePurchasesUseCase(private val inAppRepository: InAppBillingRepository) {
    suspend operator fun invoke(
        productIds: List<String>,
        type: String = BillingClient.ProductType.INAPP
    ): Flow<Boolean> {
        return inAppRepository.queryActivePurchases(productIds, type)
    }
}
