package com.example.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Date

/**
 * Professional AdMob Manager for RA7ALAT DZ
 * Built with user convenience and smooth experience in mind.
 */
object AdMobManager : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private const val TAG = "AdMobManager"

    // Real AdMob Unit IDs provided by the user:
    const val APP_ID = "ca-app-pub-5848825848415781~7369953586"
    const val APP_OPEN_AD_ID = "ca-app-pub-5848825848415781/9804545239"
    const val BANNER_AD_ID = "ca-app-pub-5848825848415781/6417738067"
    const val INTERSTITIAL_AD_ID = "ca-app-pub-5848825848415781/2603500613"

    /**
     * مفتاح التحكم في الإعلانات (Master Ads Switch):
     * - عند التعيين إلى (false): يتم تعطيل كافة الإعلانات تماماً.
     * - عند التعيين إلى true: يتم تفعيل الإعلانات.
     */
    var areAdsEnabled: Boolean = false

    // Official Google Test IDs (activated automatically on test devices or while account is pending):
    const val TEST_APP_OPEN_AD_ID = "ca-app-pub-3940256099942544/9257395921"
    const val TEST_BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111"

    private var currentActivity: Activity? = null
    private var isInitialized = false

    // App Open Ad variables
    private var appOpenAd: AppOpenAd? = null
    private var isAppOpenLoading = false
    private var isAppOpenShowing = false
    private var appOpenLoadTime: Long = 0

    // Interstitial Ad variables
    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    private var lastInterstitialShowTime: Long = 0
    private var tripViewCounter = 0

    // Minimum time between interstitials (2.5 minutes) for maximum user comfort
    private const val INTERSTITIAL_COOLDOWN_MS = 150_000L

    /**
     * Initialize Google Mobile Ads SDK and lifecycle observers
     */
    fun initialize(application: Application) {
        if (!areAdsEnabled) {
            Log.d(TAG, "AdMobManager: Ads are currently disabled (areAdsEnabled = false).")
            return
        }
        if (isInitialized) return
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        try {
            // Register Samsung M35 and testing identifiers
            val testDeviceIds = listOf(
                AdRequest.DEVICE_ID_EMULATOR,
                "c2d65ecc-a1a0-4ee4-9df9-47af18967cec",
                "C2D65ECC-A1A0-4EE4-9DF9-47AF18967CEC",
                "c2d65ecca1a04ee49df947af18967cec",
                "C2D65ECCA1A04EE49DF947AF18967CEC"
            )
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            MobileAds.initialize(application) { status ->
                Log.d(TAG, "AdMob MobileAds initialized: $status")
                isInitialized = true
                // Preload ads
                loadAppOpenAd(application)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AdMob: ${e.message}", e)
        }
    }

    // ==========================================
    // 1. APP OPEN AD
    // ==========================================
    fun loadAppOpenAd(context: Context, useFallbackIfFailed: Boolean = true) {
        if (!areAdsEnabled || isAppOpenLoading || isAppOpenAdAvailable()) return
        isAppOpenLoading = true

        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            APP_OPEN_AD_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isAppOpenLoading = false
                    appOpenLoadTime = Date().time
                    Log.d(TAG, "Real App Open Ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Real App Open Ad failed to load (${error.code}): ${error.message}")
                    if (useFallbackIfFailed) {
                        Log.d(TAG, "Trying official Google test App Open Ad ID...")
                        AppOpenAd.load(
                            context,
                            TEST_APP_OPEN_AD_ID,
                            request,
                            object : AppOpenAd.AppOpenAdLoadCallback() {
                                override fun onAdLoaded(testAd: AppOpenAd) {
                                    appOpenAd = testAd
                                    isAppOpenLoading = false
                                    appOpenLoadTime = Date().time
                                    Log.d(TAG, "Test App Open Ad loaded successfully on test device")
                                }

                                override fun onAdFailedToLoad(testError: LoadAdError) {
                                    appOpenAd = null
                                    isAppOpenLoading = false
                                    Log.w(TAG, "Test App Open Ad failed: ${testError.message}")
                                }
                            }
                        )
                    } else {
                        appOpenAd = null
                        isAppOpenLoading = false
                    }
                }
            }
        )
    }

    private fun isAppOpenAdAvailable(): Boolean {
        // App open ads expire after 4 hours
        val isNotExpired = (Date().time - appOpenLoadTime) < (4 * 3600 * 1000)
        return appOpenAd != null && isNotExpired
    }

    fun showAppOpenAdIfAvailable(activity: Activity, onDismissed: () -> Unit = {}) {
        if (!areAdsEnabled) {
            onDismissed()
            return
        }
        if (isAppOpenShowing) return
        if (!isAppOpenAdAvailable()) {
            loadAppOpenAd(activity.applicationContext)
            onDismissed()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isAppOpenShowing = false
                loadAppOpenAd(activity.applicationContext)
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                appOpenAd = null
                isAppOpenShowing = false
                loadAppOpenAd(activity.applicationContext)
                onDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                isAppOpenShowing = true
            }
        }
        isAppOpenShowing = true
        appOpenAd?.show(activity)
    }

    // When the app comes to foreground from background
    override fun onStart(owner: LifecycleOwner) {
        if (!areAdsEnabled) return
        currentActivity?.let { activity ->
            Log.d(TAG, "App came to foreground (onStart) - showing App Open Ad if available")
            showAppOpenAdIfAvailable(activity)
        }
    }

    // ==========================================
    // 2. INTERSTITIAL AD
    // ==========================================
    fun loadInterstitialAd(context: Context) {
        if (!areAdsEnabled || interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_ID,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial Ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w(TAG, "Interstitial Ad failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Show interstitial ad with frequency capping and cooldown to protect user comfort
     */
    fun showInterstitialAd(activity: Activity, force: Boolean = false, onDismissed: () -> Unit = {}) {
        if (!areAdsEnabled) {
            onDismissed()
            return
        }
        val now = System.currentTimeMillis()
        if (!force && (now - lastInterstitialShowTime < INTERSTITIAL_COOLDOWN_MS)) {
            // User viewed an ad recently, do not disturb
            onDismissed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastInterstitialShowTime = System.currentTimeMillis()
                    loadInterstitialAd(activity.applicationContext)
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadInterstitialAd(activity.applicationContext)
                    onDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    lastInterstitialShowTime = System.currentTimeMillis()
                }
            }
            ad.show(activity)
        } else {
            loadInterstitialAd(activity.applicationContext)
            onDismissed()
        }
    }

    /**
     * Smart trigger: Called when user views trips or performs actions
     */
    fun incrementTripInteraction(context: Context) { if (context is Activity) onTripInteraction(context) }
    fun onTripInteraction(context: Context) { if (context is Activity) onTripInteraction(context) }
    fun onTripInteraction(activity: Activity) {
        if (!areAdsEnabled) return
        tripViewCounter++
        // Show after every 4 trip interactions and only if cooldown passed
        if (tripViewCounter % 4 == 0) {
            showInterstitialAd(activity)
        }
    }

    // ==========================================
    // ACTIVITY LIFECYCLE TRACKING
    // ==========================================
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}

/**
 * Reusable Compose Banner Ad View
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.BANNER_AD_ID,
    adSize: AdSize = AdSize.BANNER
) {
    if (!AdMobManager.areAdsEnabled) return
    val context = LocalContext.current
    var currentAdView by remember { mutableStateOf<AdView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            currentAdView?.destroy()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(vertical = 2.dp)
            .testTag("admob_banner_box"),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("admob_banner_view"),
            factory = { ctx ->
                val container = android.widget.FrameLayout(ctx).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                fun attachAndLoadAd(targetUnitId: String) {
                    try {
                        val adView = AdView(ctx).apply {
                            setAdSize(adSize)
                            this.adUnitId = targetUnitId
                            layoutParams = android.widget.FrameLayout.LayoutParams(
                                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = android.view.Gravity.CENTER
                            }
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    super.onAdLoaded()
                                    Log.d("AdMobBanner", "Banner loaded successfully with unit: $targetUnitId")
                                }

                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    super.onAdFailedToLoad(error)
                                    Log.w("AdMobBanner", "Banner unit ($targetUnitId) failed (${error.code}): ${error.message}")
                                    if (targetUnitId != AdMobManager.TEST_BANNER_AD_ID) {
                                        Log.d("AdMobBanner", "Creating clean fallback test banner...")
                                        container.post {
                                            container.removeAllViews()
                                            attachAndLoadAd(AdMobManager.TEST_BANNER_AD_ID)
                                        }
                                    }
                                }
                            }
                        }
                        currentAdView?.destroy()
                        currentAdView = adView
                        container.addView(adView)
                        adView.loadAd(AdRequest.Builder().build())
                    } catch (e: Exception) {
                        Log.w("AdMobBanner", "Failed to initialize AdView: ${e.message}")
                    }
                }

                attachAndLoadAd(adUnitId)
                container
            }
        )
    }
}

@Composable
fun BannerAdView(modifier: Modifier = Modifier) = AdMobBanner(modifier = modifier)
