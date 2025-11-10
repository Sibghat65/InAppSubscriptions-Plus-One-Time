package com.sibghat.test.inappfullsubscription.domain.repositories

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.sibghat.test.inappfullsubscription.data.remote.repositories.InAppBillingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Implementation of the In-App Billing Repository using Google Play BillingClient.
 * This class handles billing setup, querying products, managing purchases,
 * acknowledging and consuming purchases, and retry logic for disconnections.
 */

class InAppBillingRepositoryImp(context: Context) : PurchasesUpdatedListener, InAppBillingRepository {
    companion object {
        private const val TAG = "InAppRepository"
    }

    // Instance of BillingClient responsible for communicating with Google Play.
    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this) // Set the listener to receive purchase updates.
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts() // Allow one-time (non-subscription) purchases.
                .enablePrepaidPlans() // Allow prepaid subscription plans.
                .build()
        )
        .build()

    // Callback function to notify about purchase results.
    private var purchaseCallback: ((Boolean, Purchase?) -> Unit)? = null

    // Retry mechanism fields.
    private var retryCount = 0
    private val maxRetries = 3


    /**
     * Starts a connection to the Google Play Billing service.
     * Retries up to [maxRetries] times in case of disconnection.
     */
    fun startConnection(onConnected: (() -> Unit)? = null) {
        billingClient.startConnection(object : BillingClientStateListener {

            // Called when the billing setup process is finished.
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "BillingClient connected")
                    retryCount = 0 // Reset retry counter after successful connection.
                    onConnected?.invoke() // Notify that billing is connected.
                } else {
                    Log.e(TAG, "Billing setup failed: ${result.debugMessage}")
                }
            }

            // Called when the billing service disconnects (e.g., network issues).
            override fun onBillingServiceDisconnected() {
                retryCount++
                if (retryCount <= maxRetries) {
                    // Exponential retry delay: increases with each attempt.
                    val delay = retryCount * 2000L
                    Handler(Looper.getMainLooper()).postDelayed({
                        startConnection(onConnected)
                    }, delay)
                } else {
                    Log.e(TAG, "Failed to reconnect after $maxRetries retries")
                }
            }
        })
    }

    /**
     * Coroutine Flow version of [startConnection].
     * Emits `true` once the connection is successfully established.
     */
    override suspend fun connectFlow(): Flow<Boolean> = callbackFlow {
        startConnection {
            trySend(true)
            close()
        }
        awaitClose()
    }


    /**
     * Queries product details (e.g., price, description) from Google Play Console.
     * @param productIds List of product IDs to query.
     * @param type Product type (e.g., INAPP or SUBS).
     */
    override suspend fun queryProducts(
        productIds: List<String>,
        type: String
    ): Flow<List<ProductDetails>> = callbackFlow {

        // Build the query parameters with the list of product IDs and type.
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(type)
                        .build()
                }
            ).build()

        // Query asynchronously for product details.
        billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                // Successfully retrieved product details.
                trySend(productDetailsList.productDetailsList)
            } else {
                Log.e(TAG, "queryProducts() failed: ${result.debugMessage}")
                trySend(emptyList())
            }
            close()
        }
        awaitClose()
    }




    /**
     * Launches the billing flow for a specific product or subscription.
     * Emits `true` if the purchase process starts successfully.
     */
    override suspend fun purchaseFlow(
        activity: Activity,
        productDetails: ProductDetails,
        type: String,
        basePlanId: String?
    ): Flow<Boolean> = callbackFlow {

        // Build parameters for the selected product.
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)

        // For subscriptions, set the offer token related to the selected base plan.
        if (type == BillingClient.ProductType.SUBS && basePlanId != null) {
            val offerToken = productDetails.subscriptionOfferDetails
                ?.firstOrNull { it.basePlanId == basePlanId }
                ?.offerToken

            if (offerToken == null) {
                Log.e(TAG, "No offer token found for base plan $basePlanId")
                trySend(false)
                close()
                return@callbackFlow
            }
            productParams.setOfferToken(offerToken)
        }

        // Create billing flow parameters.
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams.build()))
            .build()

        // Launch the billing flow.
        val result = billingClient.launchBillingFlow(activity, billingFlowParams)

        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            // Set callback to handle the purchase result.
            purchaseCallback = { success, _ ->
                trySend(success)
                close()
            }
        } else {
            Log.e(TAG, "Launch flow failed: ${result.debugMessage}")
            trySend(false)
            close()
        }
        awaitClose()
    }

    /**
     * Called automatically when a purchase is completed or fails.
     * Handles purchase acknowledgement and state updates.
     */
    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { handlePurchase(it) }
        } else {
            Log.e(TAG, "Purchase failed or canceled: ${result.debugMessage}")
            purchaseCallback?.invoke(false, null)
        }
    }

    /**
     * Handles post-purchase logic such as acknowledging a purchase.
     */
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                // Acknowledge purchase to confirm it to Google Play.
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(params) { result ->
                    val success = result.responseCode == BillingClient.BillingResponseCode.OK
                    Log.d(TAG, "Acknowledged: $success")
                    purchaseCallback?.invoke(success, if (success) purchase else null)
                }
            } else {
                // Already acknowledged — consider it successful.
                purchaseCallback?.invoke(true, purchase)
            }
        } else {
            // Purchase was not completed successfully.
            purchaseCallback?.invoke(false, purchase)
        }
    }

    /**
     * Queries if there are any active (purchased & acknowledged) items for the given product IDs.
     */
    override suspend fun queryActivePurchases(
        productIds: List<String>,
        type: String
    ): Flow<Boolean> = callbackFlow {

        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(type)
                .build()
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                // Check if any product is active and acknowledged.
                val active = purchases.any { p ->
                    p.products.any { it in productIds } &&
                            p.purchaseState == Purchase.PurchaseState.PURCHASED &&
                            p.isAcknowledged
                }
                trySend(active)
            } else {
                Log.e(TAG, "queryActivePurchases() failed: ${result.debugMessage}")
                trySend(false)
            }
            close()
        }
        awaitClose()
    }

    /**
     * Consumes a previously purchased consumable product.
     * This allows it to be purchased again (for one-time items like coins, etc.).
     */
    override suspend fun consumePurchase(purchaseToken: String): Flow<Boolean> = callbackFlow {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient.consumeAsync(consumeParams) { result, _ ->
            val success = result.responseCode == BillingClient.BillingResponseCode.OK
            Log.d(TAG, "Consume result: ${result.debugMessage}")
            trySend(success)
            close()
        }
        awaitClose()
    }
    /** Ends the billing client connection. */
    override suspend fun endConnection() {
        billingClient.endConnection()
    }
}
