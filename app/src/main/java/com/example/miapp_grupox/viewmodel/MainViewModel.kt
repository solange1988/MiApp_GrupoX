package com.example.miapp_grupox.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miapp_grupox.model.DatabaseProvider
import com.example.miapp_grupox.model.FlusingEntity
import com.example.miapp_grupox.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = MainRepository(
        DatabaseProvider.getDatabase(application).flusingDao()
    )

    private val _registros =
        MutableStateFlow<List<FlusingEntity>>(emptyList())

    val registros: StateFlow<List<FlusingEntity>> =
        _registros.asStateFlow()

    init {
        // La pantalla sigue leyendo desde Room, como antes
        viewModelScope.launch {
            repository.obtenerRegistros().collect { datos ->
                _registros.value = datos
            }
        }
        // Al abrir, trae los registros de ms-flushing
        sincronizar()
    }

    // Vuelve a pedir los registros al servidor
    fun sincronizar() {
        viewModelScope.launch {
            if (!repository.sincronizar()) {
                avisar("No se pudo conectar con el servidor. Se muestran los datos guardados en el teléfono.")
            }
        }
    }

    fun guardarRegistro(registro: FlusingEntity) {
        viewModelScope.launch {
            if (!repository.guardarRegistro(registro)) {
                avisar("No se pudo guardar el registro en el servidor.")
            }
        }
    }

    // Corrige solo la observación de un registro ya guardado
    fun actualizarObservacion(registro: FlusingEntity, nuevaObservacion: String) {
        viewModelScope.launch {
            val ok = repository.actualizarRegistro(
                registro.copy(observaciones = nuevaObservacion.trim())
            )
            if (!ok) {
                avisar("No se pudo actualizar el registro en el servidor.")
            }
        }
    }

    fun eliminarRegistro(id: Int) {
        viewModelScope.launch {
            if (!repository.eliminarRegistro(id)) {
                avisar("No se pudo eliminar el registro en el servidor.")
            }
        }
    }

    private fun avisar(mensaje: String) {
        Toast.makeText(getApplication<Application>(), mensaje, Toast.LENGTH_LONG).show()
    }
}