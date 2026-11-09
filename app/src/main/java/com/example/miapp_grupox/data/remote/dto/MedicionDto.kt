package com.example.miapp_grupox.data.remote.dto

// Lo que responde ms-monitoreo (puerto 8082)
data class MedicionDto(
    val id: Long? = null,
    val lineaId: Long,
    val temperatura: Double,
    val estado: String? = null,     // lo calcula el backend
    val fechaHora: String? = null   // ej: "2026-11-01T08:00:00"
)

data class AlertaDto(
    val medicionId: Long?,
    val lineaId: Long,
    val nivel: String,
    val temperatura: Double,
    val mensaje: String,
    val fechaHora: String
)