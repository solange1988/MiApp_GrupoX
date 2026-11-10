package com.example.miapp_grupox.data.remote.dto

import com.google.gson.annotations.SerializedName

// Respuesta de Open-Meteo (solo los campos que usamos)
data class ClimaResponse(
    val current: ClimaActual
)

data class ClimaActual(
    val time: String,
    @SerializedName("temperature_2m") val temperatura: Double,
    @SerializedName("relative_humidity_2m") val humedad: Int
)