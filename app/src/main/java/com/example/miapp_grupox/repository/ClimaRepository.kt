package com.example.miapp_grupox.repository

import com.example.miapp_grupox.data.remote.RetrofitClient
import com.example.miapp_grupox.data.remote.api.ClimaApi
import com.example.miapp_grupox.data.remote.dto.ClimaActual

class ClimaRepository(
    private val api: ClimaApi = RetrofitClient.climaApi
) {

    // Coordenadas de Melipilla (zona de las granjas del caso)
    suspend fun obtenerClimaMelipilla(): ClimaActual =
        api.obtenerClima(latitud = -33.69, longitud = -71.21).current
}