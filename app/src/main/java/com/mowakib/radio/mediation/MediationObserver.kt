package com.mowakib.radio.mediation

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSource.isInterstitialReady
import com.ironsource.mediationsdk.integration.IntegrationHelper
import kotlinx.coroutines.delay

class MediationObserver(
    private val activity: Activity,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope
) : LifecycleObserver {

    private var mInterstitialAd: InterstitialAd? = null

    init {
        initAdmob()
        initIronSource()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() = showAdsWithDelay()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() = IronSource.onResume(activity)

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() = IronSource.onPause(activity)

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() = showIronSourceInterAds()

    private fun initAdmob() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            ADMOB_INTER,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun initIronSource() {
        IronSource.init(activity, IRON_APP_KEY, IronSource.AD_UNIT.INTERSTITIAL)
        IronSource.loadInterstitial()
        IronSource.setConsent(true)
        IntegrationHelper.validateIntegration(activity)
    }

    private fun showIronSourceInterAds() {
        if (isInterstitialReady()) {
            IronSource.showInterstitial()
        } else {
            IronSource.loadInterstitial()
        }
    }

    private fun showAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
        } else {
            showIronSourceInterAds()
        }
    }

    private fun showAdsWithDelay() {
        lifecycleCoroutineScope.launchWhenResumed {
            delay(DELAY)
            showAds()
        }
    }

    companion object {
        private const val IRON_APP_KEY = "ee5d3791"
        private const val ADMOB_INTER = "ca-app-pub-3940256099942544/1033173712"
        private const val DELAY = 1000 * 30 * 1L // 30..sec
    }

}
