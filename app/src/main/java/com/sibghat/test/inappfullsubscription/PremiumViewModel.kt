package com.sibghat.test.inappfullsubscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.sibghat.test.inappfullsubscription.domain.usecases.ConnectBillingUseCase
import com.sibghat.test.inappfullsubscription.domain.usecases.PurchaseProductUseCase
import com.sibghat.test.inappfullsubscription.domain.usecases.QueryActivePurchasesUseCase
import com.sibghat.test.inappfullsubscription.domain.usecases.QueryProductsUseCase
import com.sibghat.test.inappfullsubscription.domain.usecases.TerminateConnectionUseCase
import com.sibghat.test.inappfullsubscription.utils.lifeTimeProductId
import com.sibghat.test.inappfullsubscription.utils.monthlyProductId
import com.sibghat.test.inappfullsubscription.utils.yearlyProductId
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PremiumViewModel(
    private val connectBillingUseCase: ConnectBillingUseCase,
    private val queryProductsUseCase: QueryProductsUseCase,
    private val purchaseProductUseCase: PurchaseProductUseCase,
    private val terminateConnectionUseCase: TerminateConnectionUseCase,
    private val queryActivePurchasesUseCase: QueryActivePurchasesUseCase
) : ViewModel() {

    // Holds product details for UI to display
    private val _productDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetails: StateFlow<List<ProductDetails>> = _productDetails

    // Indicates whether the product has already been purchased
    private val _isPurchased = MutableStateFlow(false)
    val isPurchased: StateFlow<Boolean> = _isPurchased

    // Emits true when a purchase is successful (used for triggering UI events)
    private val _purchaseSuccess = MutableSharedFlow<Boolean>()
    val purchaseSuccess: SharedFlow<Boolean> = _purchaseSuccess


    fun connectToBilling(){
        viewModelScope.launch {
            connectBillingUseCase.invoke().collect {isConnected->
                if (isConnected){
                    //is connection successful you can directly call for is purchased or not here otherwise you can use the usecase in separate functions
                    connectBillingUseCase.invoke().collect { billingConnected ->
                        if (billingConnected) {
                            val oneTimeFlow = queryActivePurchasesUseCase(
                                productIds = listOf(
                                    lifeTimeProductId,//here pass product ids for one time products
                                ),
                                type = BillingClient.ProductType.INAPP
                            )
                            val subsFlow = queryActivePurchasesUseCase(
                                productIds = listOf(
                                    monthlyProductId,
                                    yearlyProductId,//any other subscriptions ids here
                                ),
                                type = BillingClient.ProductType.SUBS
                            )
                            combine(oneTimeFlow, subsFlow) { inApp, subs ->
                                inApp || subs
                            }.collect { isPremiumUser ->
                                //set your user status according to the isPremiumUser flow result
                                _isPurchased.value = isPremiumUser
                            }
                        }
                    }
                }

            }
        }
    }

    fun fetchProductsDetails(oneTimeProductsIdsList: List<String>,subscriptionsProductsIdsList: List<String>){
        viewModelScope.launch {
            connectBillingUseCase.invoke().collect {isConnected->
                if (isConnected){
                    val inAppFlow = queryProductsUseCase(
                        productIds = oneTimeProductsIdsList,
                        type = BillingClient.ProductType.INAPP
                    )
                    val subsFlow = queryProductsUseCase(
                        productIds = subscriptionsProductsIdsList,
                        type = BillingClient.ProductType.SUBS
                    )
                    combine(inAppFlow, subsFlow) { inApp, subs ->
                        inApp + subs
                    }.collect { allProducts ->
                        _productDetails.value = allProducts
                    }
                }
            }
        }
    }

    fun purchaseProducts(productDetails: ProductDetails,activity: Activity,type: String,basePlanId: String? = null){
        viewModelScope.launch {
            purchaseProductUseCase.invoke(
                activity = activity,
                productDetails = productDetails,
                type = type,
                basePlanId = basePlanId
            ).collect {isPurchased->
                //if successfully purchased product or subscribed
                _isPurchased.value = isPurchased
            }
        }
    }
    fun terminateConnection(){
        viewModelScope.launch {
            terminateConnectionUseCase.invoke()
        }
    }
}