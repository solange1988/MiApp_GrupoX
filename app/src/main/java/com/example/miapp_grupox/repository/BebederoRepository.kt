package com.example.miapp_grupox.repository





import com.example.miapp_grupox.model.LineaBebedero

// Simula el origen de los datos (más adelante se puede reemplazar por una API real)
class BebederoRepository {

    // Lista de líneas de bebederos, con datos ficticios para el MVP académico
    private val lineas = listOf(
        LineaBebedero(
            granja = "Granja Melipilla",
            galpon = "Galpón A",
            nombre = "Línea 1",
            temperatura = 22.0,
            estado = "Normal"
        ),
        LineaBebedero(
            granja = "Granja Melipilla",
            galpon = "Galpón A",
            nombre = "Línea 2",
            temperatura = 28.0,
            estado = "Advertencia"
        ),
        LineaBebedero(
            granja = "Granja Melipilla",
            galpon = "Galpón B",
            nombre = "Línea 3",
            temperatura = 32.0,
            estado = "Critico"
        ),
        LineaBebedero(
            granja = "Granja Santiago",
            galpon = "Galpón A",
            nombre = "Línea 1",
            temperatura = 21.5,
            estado = "Normal"
        )
    )

    // Devuelve todas las líneas registradas
    fun obtenerLineas(): List<LineaBebedero> {
        return lineas
    }

    // Devuelve solo las líneas que están en estado crítico o advertencia (para la pantalla de alertas)
    fun obtenerAlertas(): List<LineaBebedero> {
        return lineas.filter { it.estado == "Advertencia" || it.estado == "Critico" }
    }
}