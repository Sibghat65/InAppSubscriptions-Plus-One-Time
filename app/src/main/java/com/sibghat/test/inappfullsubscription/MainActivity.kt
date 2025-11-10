package com.sibghat.test.inappfullsubscription

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.billingclient.api.BillingClient
import com.sibghat.test.inappfullsubscription.ui.theme.InAppFullSubscriptionTheme
import com.sibghat.test.inappfullsubscription.utils.lifeTimeProductId
import com.sibghat.test.inappfullsubscription.utils.monthlyBasePlanId
import com.sibghat.test.inappfullsubscription.utils.monthlyProductId
import com.sibghat.test.inappfullsubscription.utils.yearlyProductId
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var oneTimePriceAndCode by remember { mutableStateOf("$0") }
            var monthlyPriceAndCode by remember { mutableStateOf("$0") }
            var yearlyPriceAndCode by remember { mutableStateOf("$0") }


            val viewModel: PremiumViewModel = koinViewModel()

            //required in premium screens like you want to update UI with product details etc
            val productDetails by viewModel.productDetails.collectAsStateWithLifecycle()
            //if you want to collect flow for premium or not
            val isPremium by viewModel.isPurchased.collectAsStateWithLifecycle()


            //first call for start connecting for billing client
            //isPurchased is already implemented in connect to Billing but if you can you can do separate
            //method here after connectToBilling
            viewModel.connectToBilling()


            //just like connect to billing there is also method to terminate connection if no required further
            //this will release the resource used by billing client
            viewModel.terminateConnection()

            //query product details list
            viewModel.fetchProductsDetails(
                listOf(lifeTimeProductId), listOf(
                    monthlyProductId,
                    yearlyProductId
                )
            )




            //you can use the product details in show in UI accordingly
            if (productDetails.isNotEmpty()) {
                val oneTimeProductDetails =
                    productDetails.firstOrNull { it.productId == lifeTimeProductId }
                val monthlyProductDetails =
                    productDetails.firstOrNull { it.productId == monthlyProductId }
                val yearlyProductDetails =
                    productDetails.firstOrNull { it.productId == yearlyProductId }
                oneTimePriceAndCode =
                    oneTimeProductDetails?.oneTimePurchaseOfferDetails?.formattedPrice ?: "$0"
                monthlyPriceAndCode =
                    monthlyProductDetails?.subscriptionOfferDetails
                        ?.lastOrNull()
                        ?.pricingPhases
                        ?.pricingPhaseList
                        ?.lastOrNull()
                        ?.formattedPrice ?: "$0"
                yearlyPriceAndCode =
                    yearlyProductDetails?.subscriptionOfferDetails
                        ?.lastOrNull()
                        ?.pricingPhases
                        ?.pricingPhaseList
                        ?.lastOrNull()
                        ?.formattedPrice ?: "$0"
            }




            InAppFullSubscriptionTheme {
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            if (productDetails.isNotEmpty()){
                                //if life time is selected
                                val details = productDetails.firstOrNull { it.productId == lifeTimeProductId }

                                //or if subscription selected then according to that product details can be
                                //val details = productDetails.firstOrNull { it.productId == monthlyProductId }
                                //val details = productDetails.firstOrNull { it.productId == yearlyProductId }

                                details?.let {
                                    viewModel.purchaseProducts(productDetails = details,this@MainActivity,
                                        BillingClient.ProductType.INAPP, basePlanId = null
                                    )

                                    //if subscription is selected
                                    viewModel.purchaseProducts(productDetails = details,this@MainActivity,
                                        BillingClient.ProductType.INAPP, basePlanId = monthlyBasePlanId// or yearly as your requirements
                                    )
                                }

                            }else{
                             //product details are empty no products
                            }
                        }) {
                            Text(
                                text = "Subscribe",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
