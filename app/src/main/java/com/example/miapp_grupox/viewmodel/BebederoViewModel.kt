package com.example.miapp_grupox.viewmodel



import androidx.lifecycle.ViewModel
import com.example.miapp_grupox.model.LineaBebedero
import com.example.miapp_grupox.repository.BebederoRepository

// ViewModel: hace de puente entre el Repository (datos) y las pantallas (UI)
class BebederoViewModel : ViewModel() {

    // Una sola instancia del repositorio para todo el ViewModel
    private val repository = BebederoRepository()

    // Devuelve todas las líneas (para TemperaturasScreen)
    fun obtenerLineas(): List<LineaBebedero> {
        return repository.obtenerLineas()
    }

    // Devuelve solo las líneas con alertas (para AlertasScreen)
    fun obtenerAlertas(): List<LineaBebedero> {
        return repository.obtenerAlertas()
    }

    // Guarda un registro de flushing (por ahora solo simula el registro)
    fun registrarFlushing(nombreLinea: String, observacion: String): Boolean {
        if (nombreLinea.isEmpty() || observacion.isBlank()) {
            return false
        }
        // Aquí más adelante se guardará en la base de datos local (Room)
        return true
    }
}