package com.sibghat.test.inappfullsubscription.domain.usecases

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.sibghat.test.inappfullsubscription.data.remote.repositories.InAppBillingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Handles initiating the purchase flow for both one-time products (INAPP) and subscriptions (SUBS).
 *
 * This UseCase delegates the purchase process to the [InAppBillingRepository], which manages
 * interaction with the Google Play Billing Library. It is responsible for launching the billing
 * flow using the provided [Activity] context and [ProductDetails].
 *
 * ⚙️ Usage:
 * - Call this to start the purchase flow for a selected product.
 * - Supports both one-time purchases and subscriptions via the [type] parameter.
 * - Optionally, provide a [basePlanId] for subscription offers.
 *
 * ⚠️ Note:
 * - Ensure the billing connection is established before calling this UseCase (see [ConnectBillingUseCase]).
 * - The returned [Flow<Boolean>] indicates the success or failure of the purchase process.
 */
class PurchaseProductUseCase(private val inAppRepository: InAppBillingRepository) {
    suspend operator fun invoke(
        activity: Activity,
        productDetails: ProductDetails,
        type: String = BillingClient.ProductType.INAPP,
        basePlanId: String? = null
    ): Flow<Boolean> {
        return inAppRepository.purchaseFlow(activity, productDetails, type, basePlanId)
    }
}
