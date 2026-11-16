package com.example.miapp_grupox.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// MARCA ARIZTÍA (rojo del prototipo)
// ============================================================

val AriztiaRed = Color(0xFFE30613)

val AriztiaDarkRed = Color(0xFFB5121B)

val AriztiaLightRed = Color(0xFFFDE8E9)


// ============================================================
// FONDOS Y TEXTOS (tonos cálidos del prototipo)
// ============================================================

val AppBackground = Color(0xFFF4F2EF)

val CardBackground = Color(0xFFFFFFFF)

// Tarjeta oscura de resumen ("Estado de la operación")
val DarkCard = Color(0xFF2B2927)

val DarkCardText = Color(0xFFB9B4AE)

val TextPrimary = Color(0xFF1C1B1A)

val TextSecondary = Color(0xFF8A847D)

val BorderColor = Color(0xFFE8E4DF)


// ============================================================
// ESTADO NORMAL (verde)
// ============================================================

val EstadoNormal = Color(0xFF2E7D32)

val EstadoNormalBackground = Color(0xFFE6F2E7)


// ============================================================
// ESTADO ADVERTENCIA (naranjo, "Atención" en el prototipo)
// ============================================================

val EstadoAdvertencia = Color(0xFFF57C00)

val EstadoAdvertenciaTexto = Color(0xFFB45309)

val EstadoAdvertenciaBackground = Color(0xFFFFF0E0)


// ============================================================
// ESTADO CRÍTICO (rojo)
// ============================================================

val EstadoCritico = Color(0xFFD32F2F)

val EstadoCriticoBackground = Color(0xFFFCE8E7)


// Color principal según el estado de una línea
fun colorEstado(estado: String): Color = when (estado.trim().lowercase()) {
    "crítico", "critico" -> EstadoCritico
    "advertencia" -> EstadoAdvertencia
    else -> EstadoNormal
}

// Color de fondo suave según el estado
fun fondoEstado(estado: String): Color = when (estado.trim().lowercase()) {
    "crítico", "critico" -> EstadoCriticoBackground
    "advertencia" -> EstadoAdvertenciaBackground
    else -> EstadoNormalBackground
}