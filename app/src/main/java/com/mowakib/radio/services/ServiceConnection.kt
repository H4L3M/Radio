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

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.MutableLiveData


class ServiceConnection(context: Context) {
    val isConnected = MutableLiveData<Boolean>()
        .apply { postValue(false) }


    companion object {
        // For Singleton instantiation.
        @Volatile
        private var instance: ServiceConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: ServiceConnection(context)
                    .also { instance = it }
            }
    }
}
