package com.example.miapp_grupox.data.remote.dto

// Lo que responde ms-flushing (puerto 8083)
data class FlushingDto(
    val id: Long? = null,
    val granja: String,
    val galpon: String,
    val linea: String,
    val responsable: String,
    val motivo: String,
    val observaciones: String,
    val fecha: String
)