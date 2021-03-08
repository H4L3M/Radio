package com.mowakib.radio.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import com.mowakib.radio.R

@Suppress("DEPRECATION")
class NetworkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val conn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = conn.activeNetworkInfo

        // Checks the user prefs and the network connection. Based on the result, decides whether
        // to refresh the display or keep the current display.
        // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
        if (WIFI == sPref && networkInfo?.type == ConnectivityManager.TYPE_WIFI) {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            refreshDisplay = true
            Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show()

            // If the setting is ANY network and there is a network connection
            // (which by process of elimination would be mobile), sets refreshDisplay to true.
        } else if (ANY == sPref && networkInfo != null) {
            refreshDisplay = true

            // Otherwise, the app can't download content--either because there is no network
            // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
            // is no Wi-Fi connection.
            // Sets refreshDisplay to false.
        } else {
            refreshDisplay = false
            Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show()
        }
    }


    companion object {

        const val WIFI = "Wi-Fi"
        const val ANY = "Any"
        const val SO_URL = "http://stackoverflow.com/feeds/tag?tagnames=android&sort;=newest"

        // Whether there is a Wi-Fi connection.
        private var wifiConnected = false
        // Whether there is a mobile connection.
        private var mobileConnected = false
        // Whether the display should be refreshed.
        var refreshDisplay = true

        // The user's current network preference setting.
        var sPref: String? = null
    }
}