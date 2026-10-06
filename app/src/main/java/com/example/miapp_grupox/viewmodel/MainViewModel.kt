
package com.example.miapp_grupox.viewmodel

import android.app.Application
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
        viewModelScope.launch {
            repository.obtenerRegistros().collect { datos ->
                _registros.value = datos
            }
        }
    }

    fun guardarRegistro(registro: FlusingEntity) {
        viewModelScope.launch {
            repository.guardarRegistro(registro)
        }
    }

    fun eliminarRegistro(id: Int) {
        viewModelScope.launch {
            repository.eliminarRegistro(id)
        }
    }
}