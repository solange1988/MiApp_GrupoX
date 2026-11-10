package com.example.miapp_grupox.data.remote

import com.example.miapp_grupox.data.remote.api.ClimaApi
import com.example.miapp_grupox.data.remote.api.FlushingApi
import com.example.miapp_grupox.data.remote.api.InstalacionesApi
import com.example.miapp_grupox.data.remote.api.MonitoreoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 = el "localhost" de tu PC visto desde el emulador
    private const val HOST = "http://10.0.2.2"

    private fun crear(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // ---------- Microservicios propios ----------

    val instalacionesApi: InstalacionesApi by lazy {
        crear("$HOST:8081/").create(InstalacionesApi::class.java)
    }

    val monitoreoApi: MonitoreoApi by lazy {
        crear("$HOST:8082/").create(MonitoreoApi::class.java)
    }

    val flushingApi: FlushingApi by lazy {
        crear("$HOST:8083/").create(FlushingApi::class.java)
    }

    // ---------- API externa ----------

    val climaApi: ClimaApi by lazy {
        crear("https://api.open-meteo.com/").create(ClimaApi::class.java)
    }
}