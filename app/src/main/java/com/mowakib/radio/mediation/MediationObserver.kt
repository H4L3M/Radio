package com.mowakib.radio.mediation

import android.app.Activity
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSource.isInterstitialReady
import com.ironsource.mediationsdk.integration.IntegrationHelper

class MediationObserver(private val activity: Activity) : LifecycleObserver {

    private lateinit var mAdmobInterstitialAd: InterstitialAd
    private lateinit var mFacebookInterstitialAd: com.facebook.ads.InterstitialAd

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun initMediation() {
        MobileAds.initialize(activity) { initializationStatus ->
            val statusMap = initializationStatus.adapterStatusMap
            for (adapterClass in statusMap.keys) {
                val status = statusMap[adapterClass]
                Log.d(
                    activity.javaClass.name, String.format(
                        "Adapter name: %s, Description: %s, Latency: %d",
                        adapterClass, status!!.description, status.latency
                    )
                )
            }

            // Start loading ads here...
//            mAdmobInterstitialAd = InterstitialAd(activity)
//            mAdmobInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun initIronSource() {
        IronSource.init(activity, "ee5d3791", IronSource.AD_UNIT.INTERSTITIAL)
        IronSource.loadInterstitial()
        IronSource.setConsent(true)
        IntegrationHelper.validateIntegration(activity)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun resumeIronSource() = IronSource.onResume(activity)

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun pauseIronSource() = IronSource.onPause(activity)

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun showIronSourceInterAds() {
        if (isInterstitialReady()) {
            IronSource.showInterstitial()
        }
    }
}
