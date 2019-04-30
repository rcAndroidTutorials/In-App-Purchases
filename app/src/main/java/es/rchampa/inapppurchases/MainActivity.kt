package es.rchampa.inapppurchases

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, PurchasesUpdatedListener {

    private lateinit var billingClient : BillingClient
    private lateinit var productsAdapter : ProductsAdapter

    private val skuList = listOf("candy.strawberry", "candy.lemon", "android.test.purchased", "android.test.canceled", "android.test.item_unavailable")
    private val skuSubs = listOf("subs.candies.basic", "subs.candies.all", "candy.lemon", "android.test.purchased", "android.test.canceled", "android.test.item_unavailable")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_load_products.setOnClickListener(this)
        bt_retrieve_purchases.setOnClickListener(this)
        bt_load_subs.setOnClickListener(this)

        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient
            .newBuilder(this)
            .setListener(this)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    println("BILLING | startConnection | RESULT OK")
                } else {
                    println("BILLING | startConnection | RESULT: $billingResponseCode")
                }
            }

            override fun onBillingServiceDisconnected() {
                println("BILLING | onBillingServiceDisconnected | DISCONNECTED")
            }
        })
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        println("BILLING | onPurchasesUpdated | responseCode=$responseCode")
    }

    override fun onClick(view: View?) {

        view?.let {
            when(view.id){
                R.id.bt_load_products -> loadProducts()
                R.id.bt_retrieve_purchases -> retrievePurchase()
                R.id.bt_load_subs -> loadSubs()
            }
        }


    }

    private fun initProductAdapter(skuDetailsList: List<SkuDetails>) {
        productsAdapter = ProductsAdapter(skuDetailsList) {
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(it)
                .build()
            billingClient.launchBillingFlow(this, billingFlowParams)
        }
        products.adapter = productsAdapter
    }


    private fun loadProducts(){
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()

            billingClient.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                if (responseCode == BillingClient.BillingResponse.OK) {
                    println("BILLING | querySkuDetailsAsync, responseCode: $responseCode")
                    initProductAdapter(skuDetailsList)
                } else {
                    println("BILLING | Can't querySkuDetailsAsync, responseCode: $responseCode")
                }
            }
        }
        else {
            println("BILLING | Billing Client not ready")
        }
    }

    private fun loadSubs(){
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuSubs)
                .setType(BillingClient.SkuType.SUBS)
                .build()

            billingClient.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                if (responseCode == BillingClient.BillingResponse.OK) {
                    println("BILLING | querySkuDetailsAsync, responseCode: $responseCode")
                    initProductAdapter(skuDetailsList)
                } else {
                    println("BILLING | Can't querySkuDetailsAsync, responseCode: $responseCode")
                }
            }
        }
        else {
            println("BILLING | Billing Client not ready")
        }
    }

    private fun retrievePurchase(){

        val purchasesResult = HashSet<Purchase>()
        var result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        println("BILLING | queryPurchasesAsync INAPP results: ${result?.purchasesList}")
        result?.purchasesList?.apply { purchasesResult.addAll(this) }
        if (isSubscriptionSupported()) {
            result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
            result?.purchasesList?.apply { purchasesResult.addAll(this) }
            println("BILLING | queryPurchasesAsync SUBS results: ${result?.purchasesList}")
        }
    }

    private fun isSubscriptionSupported(): Boolean {
        val responseCode =
            billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        return responseCode == BillingClient.BillingResponse.OK
    }

}
