package com.example.miapp_grupox.model

// Alerta lista para mostrar en pantalla, venga del servidor o de Room
data class AlertaUi(
    val id: Long,
    val granja: String,
    val galpon: String,
    val linea: String,
    val nivel: String,          // "Advertencia" o "Crítico"
    val temperatura: Double,
    val fecha: String
)