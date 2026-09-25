package com.example.miapp_grupox.model

// Representa una línea de bebederos dentro de una granja y galpón
data class LineaBebedero(
    val granja: String,
    val galpon: String,
    val nombre: String,
    val temperatura: Double,
    val estado: String   // "Normal", "Advertencia" o "Critico"
)