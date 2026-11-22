package com.example.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Thread-safe Initializer to initialize Google Mobile Ads SDK once upon app startup in MainActivity onCreate.
 */
object MobileAdsInitializer {
    private const val TAG = "MobileAdsInitializer"
    private val isInitialized = AtomicBoolean(false)

    fun initialize(context: Context) {
        if (!AdMobManager.areAdsEnabled) {
            Log.d(TAG, "AdMob ads are disabled (AdMobManager.areAdsEnabled = false). Skipping MobileAds SDK init.")
            return
        }
        if (isInitialized.compareAndSet(false, true)) {
            Thread {
                try {
                    // Enable test devices config if needed
                    MobileAds.initialize(context) { initializationStatus ->
                        val statusMap = initializationStatus.adapterStatusMap
                        for ((adapterClass, status) in statusMap) {
                            Log.d(TAG, "AdMob Adapter: $adapterClass - State: ${status.initializationState}")
                        }
                    }
                    Log.d(TAG, "Google Mobile Ads SDK successfully initialized.")
                } catch (e: Exception) {
                    Log.e(TAG, "Error initializing MobileAds: ${e.message}", e)
                }
            }.start()
        }
    }
}
