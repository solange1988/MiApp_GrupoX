package com.example.miapp_grupox.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.miapp_grupox.data.remote.RetrofitClient
import com.example.miapp_grupox.model.AlertaUi
import com.example.miapp_grupox.model.DatabaseProvider
import com.example.miapp_grupox.repository.AlertasRepository
import com.example.miapp_grupox.repository.BebederoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Todo lo que la pantalla necesita saber
data class AlertasUiState(
    val cargando: Boolean = false,
    val alertas: List<AlertaUi> = emptyList(),
    val mensajeError: String? = null,
    val desdeServidor: Boolean = false
)

class AlertasViewModel(
    private val repository: AlertasRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(AlertasUiState())
    val estado: StateFlow<AlertasUiState> = _estado.asStateFlow()

    fun cargarAlertas() {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true, mensajeError = null)

            _estado.value = try {
                AlertasUiState(
                    alertas = repository.obtenerAlertasRemotas(),
                    desdeServidor = true
                )
            } catch (e: Exception) {
                AlertasUiState(
                    alertas = repository.obtenerAlertasLocales(),
                    mensajeError = "No se pudo conectar con el servidor. " +
                            "Se muestran los datos guardados en el dispositivo."
                )
            }
        }
    }

    // Crea el ViewModel con sus dependencias (Retrofit + Room)
    companion object {
        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val dao = DatabaseProvider.getDatabase(application).bebederoDao()
                AlertasViewModel(
                    AlertasRepository(
                        RetrofitClient.monitoreoApi,
                        RetrofitClient.instalacionesApi,
                        BebederoRepository(dao)
                    )
                )
            }
        }
    }
}