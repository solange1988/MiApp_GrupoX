package com.example.miapp_grupox.api

data class FlushingDto(
    val id: Long? = null,
    val granja: String,
    val galpon: String,
    val linea: String,
    val responsable: String,
    val motivo: String,
    val observaciones: String? = null,
    val fecha: String
)