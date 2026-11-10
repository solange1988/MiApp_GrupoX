package com.example.miapp_grupox.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 es el "localhost" del computador visto desde el emulador de Android
    private const val URL_FLUSHING = "http://10.0.2.2:8083/"

    val flushingApi: FlushingApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_FLUSHING)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlushingApi::class.java)
    }
}