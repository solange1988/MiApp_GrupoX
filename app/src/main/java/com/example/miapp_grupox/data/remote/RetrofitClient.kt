package com.example.miapp_grupox.data.remote

import com.example.miapp_grupox.data.remote.api.FlushingApi
import com.example.miapp_grupox.data.remote.api.InstalacionesApi
import com.example.miapp_grupox.data.remote.api.MonitoreoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 = el "localhost" de tu PC visto desde el emulador
    private const val HOST = "http://10.0.2.2"

    private fun crear(puerto: Int): Retrofit =
        Retrofit.Builder()
            .baseUrl("$HOST:$puerto/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val instalacionesApi: InstalacionesApi by lazy {
        crear(8081).create(InstalacionesApi::class.java)
    }

    val monitoreoApi: MonitoreoApi by lazy {
        crear(8082).create(MonitoreoApi::class.java)
    }

    val flushingApi: FlushingApi by lazy {
        crear(8083).create(FlushingApi::class.java)
    }
}