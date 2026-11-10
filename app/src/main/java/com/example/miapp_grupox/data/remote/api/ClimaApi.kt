package com.example.miapp_grupox.data.remote.api

import com.example.miapp_grupox.data.remote.dto.ClimaResponse
import retrofit2.http.GET
import retrofit2.http.Query

// API externa: Open-Meteo (no requiere API key)
interface ClimaApi {

    @GET("v1/forecast")
    suspend fun obtenerClima(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") campos: String = "temperature_2m,relative_humidity_2m"
    ): ClimaResponse
}