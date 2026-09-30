package com.example.miapp_grupox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miapp_grupox.model.DatabaseProvider
import com.example.miapp_grupox.model.FlushingEntity
import com.example.miapp_grupox.model.LineaBebedero
import com.example.miapp_grupox.repository.BebederoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ViewModel: hace de puente entre los datos (Repository + Room) y las pantallas (UI)
class BebederoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BebederoRepository()
    private val db = DatabaseProvider.obtenerBaseDeDatos(application)

    fun obtenerLineas(): List<LineaBebedero> {
        return repository.obtenerLineas()
    }

    fun obtenerAlertas(): List<LineaBebedero> {
        return repository.obtenerAlertas()
    }

    // Valida y guarda el flushing de verdad en la base de datos local
    fun registrarFlushing(nombreLinea: String, observacion: String, onResultado: (Boolean) -> Unit) {
        if (nombreLinea.isEmpty() || observacion.isBlank()) {
            onResultado(false)
            return
        }

        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            db.flushingDao().insertar(
                FlushingEntity(
                    nombreLinea = nombreLinea,
                    observacion = observacion,
                    fecha = fechaActual
                )
            )
            onResultado(true)
        }
    }
}