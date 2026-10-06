
package com.example.miapp_grupox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
    application: Application
) : AndroidViewModel(application) {

    private val repository = BebederoRepository(
        DatabaseProvider.getDatabase(application).bebederoDao()
    )

    private val _lineas =
        MutableStateFlow<List<LineBebedero>>(emptyList())

    val lineas: StateFlow<List<LineBebedero>> =
        _lineas.asStateFlow()

    init {
        viewModelScope.launch {
            repository.inicializarDatos()

            repository.obtenerLineas().collect { datos ->
                _lineas.value = datos
            }
        }
    }

    fun guardarTemperatura(
        granja: String,
        galpon: String,
        linea: String,
        temperatura: Double
    ) {
        viewModelScope.launch {
            val existente = _lineas.value.firstOrNull {
                it.granja == granja &&
                        it.galpon == galpon &&
                        it.linea == linea
            }

            val fecha = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

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
    }
}