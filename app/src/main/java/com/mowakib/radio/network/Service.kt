package com.mowakib.radio.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mowakib.radio.utils.APPS_URL
import com.mowakib.radio.utils.BASE_URL
import com.mowakib.radio.utils.COUNTRY_URL
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DataService {
    @GET(COUNTRY_URL)
    fun getRadiosAsync(): Deferred<NetworkRadioContainer>

    @GET(APPS_URL)
    fun getAppsAsync(): Deferred<NetworkAppsContainer>
}

object Network {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val data: DataService = retrofit.create(DataService::class.java)
}