package com.sibghat.test.inappfullsubscription.data.remote.repositories

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import kotlinx.coroutines.flow.Flow

interface InAppBillingRepository {
    suspend fun connectFlow(): Flow<Boolean>
    suspend fun queryProducts(productIds: List<String>, type: String): Flow<List<ProductDetails>>
    suspend fun purchaseFlow(activity: Activity, productDetails: ProductDetails, type: String, basePlanId: String? = null): Flow<Boolean>
    suspend fun queryActivePurchases(productIds: List<String>, type: String): Flow<Boolean>
    suspend fun consumePurchase(purchaseToken: String): Flow<Boolean>
    suspend fun endConnection()

}