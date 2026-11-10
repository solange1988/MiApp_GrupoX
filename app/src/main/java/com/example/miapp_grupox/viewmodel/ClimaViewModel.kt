package com.example.miapp_grupox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miapp_grupox.repository.ClimaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ClimaUiState(
    val cargando: Boolean = false,
    val temperatura: Double? = null,
    val humedad: Int? = null,
    val error: String? = null
)

class ClimaViewModel(
    private val repository: ClimaRepository = ClimaRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow(ClimaUiState())
    val estado: StateFlow<ClimaUiState> = _estado.asStateFlow()

    fun cargarClima() {
        viewModelScope.launch {
            _estado.value = ClimaUiState(cargando = true)
            _estado.value = try {
                val clima = repository.obtenerClimaMelipilla()
                ClimaUiState(temperatura = clima.temperatura, humedad = clima.humedad)
            } catch (e: Exception) {
                ClimaUiState(error = "No se pudo obtener el clima")
            }
        }
    }
}