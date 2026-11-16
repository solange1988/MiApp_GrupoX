package com.example.miapp_grupox.viewmodel

import com.example.miapp_grupox.data.remote.dto.ClimaActual
import com.example.miapp_grupox.repository.ClimaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClimaViewModelTest {

    private lateinit var repository: ClimaRepository

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun estadoInicial_sinDatos() {
        val viewModel = ClimaViewModel(repository)

        assertNull(viewModel.estado.value.temperatura)
        assertFalse(viewModel.estado.value.cargando)
    }

    @Test
    fun cargarClima_conRespuesta_muestraTemperaturaYHumedad() {
        coEvery { repository.obtenerClimaMelipilla() } returns
                ClimaActual(time = "2026-11-10T00:15", temperatura = 16.9, humedad = 81)
        val viewModel = ClimaViewModel(repository)

        viewModel.cargarClima()

        val estado = viewModel.estado.value
        assertEquals(16.9, estado.temperatura!!, 0.0)
        assertEquals(81, estado.humedad)
        assertNull(estado.error)
        assertFalse(estado.cargando)
    }

    @Test
    fun cargarClima_conError_muestraMensaje() {
        coEvery { repository.obtenerClimaMelipilla() } throws RuntimeException("Sin internet")
        val viewModel = ClimaViewModel(repository)

        viewModel.cargarClima()

        val estado = viewModel.estado.value
        assertEquals("No se pudo obtener el clima", estado.error)
        assertNull(estado.temperatura)
        assertFalse(estado.cargando)
    }
}
