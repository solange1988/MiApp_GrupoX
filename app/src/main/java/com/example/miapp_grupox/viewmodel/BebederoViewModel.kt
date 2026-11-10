
package com.example.miapp_grupox.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.miapp_grupox.model.DatabaseProvider
import com.example.miapp_grupox.model.LineBebedero
import com.example.miapp_grupox.repository.BebederoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BebederoViewModel(
    private val repository: BebederoRepository
) : ViewModel() {

    private val _lineas = MutableStateFlow<List<LineBebedero>>(emptyList())
    val lineas: StateFlow<List<LineBebedero>> = _lineas.asStateFlow()

    // Aviso para la pantalla cuando no hay conexión con el servidor (null = todo bien)
    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    init {
        // La pantalla siempre muestra lo que hay en Room
        viewModelScope.launch {
            repository.obtenerLineas().collect { datos ->
                _lineas.value = datos
            }
        }
        // Al iniciar: datos de demostración si Room está vacío, y luego sincroniza con el servidor
        viewModelScope.launch {
            repository.inicializarDatos()
            sincronizarAhora()
        }
    }

    fun sincronizar() {
        viewModelScope.launch { sincronizarAhora() }
    }

    private suspend fun sincronizarAhora() {
        try {
            repository.sincronizarConServidor()
            _aviso.value = null
        } catch (e: Exception) {
            _aviso.value = "Sin conexión con el servidor. Se muestran los últimos datos guardados en el dispositivo."
        }
    }

    fun guardarTemperatura(
        granja: String,
        galpon: String,
        linea: String,
        temperatura: Double
    ) {
        viewModelScope.launch {
            try {
                // 1) Se envía al servidor y se refresca la lista
                repository.registrarTemperaturaEnServidor(granja, galpon, linea, temperatura)
                repository.sincronizarConServidor()
                _aviso.value = null
            } catch (e: Exception) {
                // 2) Sin conexión: se guarda solo en Room
                guardarLocal(granja, galpon, linea, temperatura)
                _aviso.value = "No se pudo enviar al servidor. La medición quedó guardada solo en el dispositivo."
            }
        }
    }

    private suspend fun guardarLocal(
        granja: String,
        galpon: String,
        linea: String,
        temperatura: Double
    ) {
        val existente = _lineas.value.firstOrNull {
            it.granja == granja && it.galpon == galpon && it.linea == linea
        }

        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        repository.guardar(
            LineBebedero(
                id = existente?.id ?: 0,
                granja = granja,
                galpon = galpon,
                linea = linea,
                temperatura = temperatura,
                fecha = fecha
            )
        )
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val dao = DatabaseProvider.getDatabase(application).bebederoDao()
                BebederoViewModel(BebederoRepository(dao))
            }
        }
    }
}