package com.example.miapp_grupox.viewmodel

import com.example.miapp_grupox.model.AlertaUi
import com.example.miapp_grupox.repository.AlertasRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlertasViewModelTest {

    private lateinit var repository: AlertasRepository

    private val alertaServidor = AlertaUi(
        id = 5L, granja = "Granja Rancagua", galpon = "Galpón 1", linea = "Línea 3",
        nivel = "Crítico", temperatura = 32.0, fecha = "02/11/2026 09:00"
    )

    private val alertaLocal = AlertaUi(
        id = 2L, granja = "Granja Melipilla", galpon = "Galpón 1", linea = "Línea 2",
        nivel = "Advertencia", temperatura = 28.0, fecha = "Datos de demostración"
    )

    @Before
    fun preparar() {
        // viewModelScope usa el hilo principal: lo cambiamos por uno de prueba
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun estadoInicial_estaVacioYSinCargar() {
        val viewModel = AlertasViewModel(repository)

        val estado = viewModel.estado.value
        assertFalse(estado.cargando)
        assertTrue(estado.alertas.isEmpty())
        assertNull(estado.mensajeError)
    }

    @Test
    fun cargarAlertas_conServidor_muestraAlertasDelServidor() {
        coEvery { repository.obtenerAlertasRemotas() } returns listOf(alertaServidor)
        val viewModel = AlertasViewModel(repository)

        viewModel.cargarAlertas()

        val estado = viewModel.estado.value
        assertEquals(listOf(alertaServidor), estado.alertas)
        assertTrue(estado.desdeServidor)
        assertFalse(estado.cargando)
        assertNull(estado.mensajeError)
    }

    @Test
    fun cargarAlertas_sinServidor_muestraAlertasLocalesYMensaje() {
        coEvery { repository.obtenerAlertasRemotas() } throws RuntimeException("Sin conexión")
        coEvery { repository.obtenerAlertasLocales() } returns listOf(alertaLocal)
        val viewModel = AlertasViewModel(repository)

        viewModel.cargarAlertas()

        val estado = viewModel.estado.value
        assertEquals(listOf(alertaLocal), estado.alertas)
        assertFalse(estado.desdeServidor)
        assertNotNull(estado.mensajeError)
        coVerify(exactly = 1) { repository.obtenerAlertasLocales() }
    }

    @Test
    fun cargarAlertas_conServidor_noUsaRoom() {
        coEvery { repository.obtenerAlertasRemotas() } returns listOf(alertaServidor)
        val viewModel = AlertasViewModel(repository)

        viewModel.cargarAlertas()

        coVerify(exactly = 0) { repository.obtenerAlertasLocales() }
    }
}
