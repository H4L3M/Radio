/*
 * Copyright 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mowakib.radio.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build

/*
You need to call the below method once. It register the callback and fire it when there is a change in network state.
Here I used a Global Static Variable, So I can use it to access the network state in anyware of the application.
*/

// Network Check
fun Context.registerNetworkCallback() {
    try {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder = NetworkRequest.Builder()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Variables.isNetworkConnected = true // Global Static Variable
                }

                override fun onLost(network: Network) {
                    Variables.isNetworkConnected = false // Global Static Variable
                }
            }
            )
        }
        Variables.isNetworkConnected = false
    } catch (e: Exception) {
        Variables.isNetworkConnected = false
    }
}

object Variables {
    // Global variable used to store network state
    var isNetworkConnected = false
}

