package com.mowakib.radio.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RadioService {
    @GET(SPECIFIC_RADIO)
    fun getRadioPlaylistAsync(): Deferred<NetworkRadioContainer>
}

object Network {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val radio = retrofit.create(RadioService::class.java)!!
}

private const val BASE_URL = "https://raw.githubusercontent.com/"
private const val SPECIFIC_RADIO = "du1p3n/radio/main/bbc"