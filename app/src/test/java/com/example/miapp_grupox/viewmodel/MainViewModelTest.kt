package com.example.miapp_grupox.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.example.miapp_grupox.api.FlushingDto
import com.example.miapp_grupox.api.toEntity
import com.example.miapp_grupox.repository.MainRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var application: Application
    private lateinit var repository: MainRepository

    private val registroEjemplo = FlushingDto(
        id = 5L,
        granja = "Granja Sur",
        galpon = "Galpon 3",
        linea = "Linea 3",
        responsable = "Pedro Rojo",
        motivo = "Mantenimiento",
        observaciones = "todo normal",
        fecha = "2026-11-09"
    ).toEntity()

    @Before
    fun preparar() {
        // Los viewModelScope usan el hilo principal: lo cambiamos por uno de prueba
        Dispatchers.setMain(UnconfinedTestDispatcher())

        // El Toast de Android no existe en los tests, lo dejamos "falso"
        mockkStatic(Toast::class)
        every {
            Toast.makeText(any<Context>(), any<CharSequence>(), any<Int>())
        } returns mockk(relaxed = true)

        application = mockk(relaxed = true)
        repository = mockk()
        every { repository.obtenerRegistros() } returns flowOf(listOf(registroEjemplo))
        coEvery { repository.sincronizar() } returns true
    }

    @After
    fun limpiar() {
        unmockkStatic(Toast::class)
        Dispatchers.resetMain()
    }

    // ---------- al iniciar ----------

    @Test
    fun alIniciar_muestraLosRegistrosDeRoom() {
        val viewModel = MainViewModel(application, repository)

        assertEquals(listOf(registroEjemplo), viewModel.registros.value)
    }

    @Test
    fun alIniciar_sincronizaConElServidor() {
        MainViewModel(application, repository)

        coVerify(exactly = 1) { repository.sincronizar() }
    }

    @Test
    fun alIniciar_siElServidorFalla_avisaConUnToast() {
        coEvery { repository.sincronizar() } returns false

        MainViewModel(application, repository)

        verify(exactly = 1) {
            Toast.makeText(any<Context>(), any<CharSequence>(), any<Int>())
        }
    }

    // ---------- guardar ----------

    @Test
    fun guardar_siTodoSaleBien_noAvisaNada() {
        coEvery { repository.guardarRegistro(any()) } returns true
        val viewModel = MainViewModel(application, repository)

        viewModel.guardarRegistro(registroEjemplo)

        coVerify(exactly = 1) { repository.guardarRegistro(registroEjemplo) }
        verify(exactly = 0) {
            Toast.makeText(any<Context>(), any<CharSequence>(), any<Int>())
        }
    }

    @Test
    fun guardar_siElServidorFalla_avisaConUnToast() {
        coEvery { repository.guardarRegistro(any()) } returns false
        val viewModel = MainViewModel(application, repository)

        viewModel.guardarRegistro(registroEjemplo)

        verify(exactly = 1) {
            Toast.makeText(any<Context>(), any<CharSequence>(), any<Int>())
        }
    }

    // ---------- actualizar observación ----------

    @Test
    fun actualizarObservacion_enviaLaObservacionSinEspacios() {
        coEvery { repository.actualizarRegistro(any()) } returns true
        val viewModel = MainViewModel(application, repository)

        viewModel.actualizarObservacion(registroEjemplo, "  revisado por supervisor  ")

        coVerify(exactly = 1) {
            repository.actualizarRegistro(match { it.observaciones == "revisado por supervisor" })
        }
    }

    @Test
    fun actualizarObservacion_siElServidorFalla_avisaConUnToast() {
        coEvery { repository.actualizarRegistro(any()) } returns false
        val viewModel = MainViewModel(application, repository)

        viewModel.actualizarObservacion(registroEjemplo, "nueva")

        verify(exactly = 1) {
            Toast.makeText(any<Context>(), any<CharSequence>(), any<Int>())
        }
    }

    // ---------- eliminar ----------

    @Test
    fun eliminar_siTodoSaleBien_noAvisaNada() {
        coEvery { repository.eliminarRegistro(any()) } returns true
        val viewModel = MainViewModel(application, repository)

        viewModel.eliminarRegistro(5)

        coVerify(exactly = 1) { repository.eliminarRegistro(5) }
        verify(exactly = 0) {
            Toast.makeText(any<Context>(), any<CharSequence>(), any<Int>())
        }
    }

    @Test
    fun eliminar_siElServidorFalla_avisaConUnToast() {
        coEvery { repository.eliminarRegistro(any()) } returns false
        val viewModel = MainViewModel(application, repository)

        viewModel.eliminarRegistro(5)

        verify(exactly = 1) {
            Toast.makeText(any<Context>(), any<CharSequence>(), any<Int>())
        }
    }
}