package com.example.miapp_grupox.data.remote.dto

// Lo que responde ms-instalaciones (puerto 8081)
data class LineaDto(
    val id: Long? = null,
    val granja: String,
    val galpon: String,
    val linea: String
)