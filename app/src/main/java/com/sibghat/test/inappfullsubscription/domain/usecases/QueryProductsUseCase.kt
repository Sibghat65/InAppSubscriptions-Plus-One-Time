package com.sibghat.test.inappfullsubscription.domain.usecases

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.sibghat.test.inappfullsubscription.data.remote.repositories.InAppBillingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Queries product details from the Google Play Billing service.
 *
 * This UseCase is responsible for retrieving information about available products
 * (either one-time purchases or subscriptions) from Google Play, based on their [productIds].
 * It uses the [InAppBillingRepository] to perform the query and returns a list of
 * [ProductDetails] objects containing metadata such as title, price, and description.
 *
 * ⚙️ Usage:
 * - Call this UseCase to fetch product details before initiating a purchase flow.
 * - Supports both INAPP (one-time) and SUBS (subscription) types via the [type] parameter.
 *
 * ⚠️ Note:
 * - Ensure the billing connection is established before calling this UseCase (see [ConnectBillingUseCase]).
 * - The returned [Flow<List<ProductDetails>>] emits a list of available product details.
 * - The list may be empty if the product IDs are invalid or not configured in Play Console.
 */
class QueryProductsUseCase(private val inAppRepository: InAppBillingRepository) {
    suspend operator fun invoke(
        productIds: List<String>,
        type: String = BillingClient.ProductType.INAPP
    ): Flow<List<ProductDetails>> {
        return inAppRepository.queryProducts(productIds, type)
    }
}
