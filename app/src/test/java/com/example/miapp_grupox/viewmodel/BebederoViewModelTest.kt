package com.example.miapp_grupox.viewmodel

import com.example.miapp_grupox.model.LineBebedero
import com.example.miapp_grupox.repository.BebederoRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BebederoViewModelTest {

    private lateinit var repository: BebederoRepository

    private val lineaEjemplo = LineBebedero(
        id = 1, granja = "Granja Melipilla", galpon = "Galpón 1", linea = "Línea 1",
        temperatura = 24.5, fecha = "01/11/2026 08:00"
    )

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        repository = mockk()
        every { repository.obtenerLineas() } returns flowOf(listOf(lineaEjemplo))
        coEvery { repository.inicializarDatos() } just Runs
        coEvery { repository.sincronizarConServidor() } just Runs
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // ---------- al iniciar ----------

    @Test
    fun alIniciar_muestraLasLineasDeRoom() {
        val viewModel = BebederoViewModel(repository)

        assertEquals(listOf(lineaEjemplo), viewModel.lineas.value)
    }

    @Test
    fun alIniciar_cargaDatosYSincronizaConElServidor() {
        val viewModel = BebederoViewModel(repository)

        coVerify(exactly = 1) { repository.inicializarDatos() }
        coVerify(exactly = 1) { repository.sincronizarConServidor() }
        assertNull(viewModel.aviso.value)
    }

    @Test
    fun alIniciar_sinServidor_muestraAviso() {
        coEvery { repository.sincronizarConServidor() } throws RuntimeException("Sin conexión")

        val viewModel = BebederoViewModel(repository)

        assertTrue(viewModel.aviso.value!!.contains("Sin conexión"))
    }

    // ---------- sincronizar ----------

    @Test
    fun sincronizar_cuandoVuelveLaConexion_quitaElAviso() {
        coEvery { repository.sincronizarConServidor() } throws RuntimeException("Sin conexión")
        val viewModel = BebederoViewModel(repository)

        coEvery { repository.sincronizarConServidor() } just Runs
        viewModel.sincronizar()

        assertNull(viewModel.aviso.value)
    }

    // ---------- guardar temperatura ----------

    @Test
    fun guardarTemperatura_conServidor_enviaLaMedicionYRefresca() {
        coEvery { repository.registrarTemperaturaEnServidor(any(), any(), any(), any()) } just Runs
        val viewModel = BebederoViewModel(repository)

        viewModel.guardarTemperatura("Granja Melipilla", "Galpón 1", "Línea 1", 32.0)

        coVerify(exactly = 1) {
            repository.registrarTemperaturaEnServidor("Granja Melipilla", "Galpón 1", "Línea 1", 32.0)
        }
        // 1 vez al iniciar + 1 vez después de guardar
        coVerify(exactly = 2) { repository.sincronizarConServidor() }
        coVerify(exactly = 0) { repository.guardar(any()) }
        assertNull(viewModel.aviso.value)
    }

    @Test
    fun guardarTemperatura_sinServidor_guardaEnRoomYMuestraAviso() {
        coEvery {
            repository.registrarTemperaturaEnServidor(any(), any(), any(), any())
        } throws RuntimeException("Sin conexión")
        val guardada = slot<LineBebedero>()
        coEvery { repository.guardar(capture(guardada)) } just Runs
        val viewModel = BebederoViewModel(repository)

        viewModel.guardarTemperatura("Granja Melipilla", "Galpón 1", "Línea 1", 32.0)

        // Reutiliza el id de la línea que ya existía en Room
        assertEquals(1, guardada.captured.id)
        assertEquals(32.0, guardada.captured.temperatura, 0.0)
        assertTrue(viewModel.aviso.value!!.contains("No se pudo enviar"))
    }

    @Test
    fun guardarTemperatura_sinServidor_lineaNueva_seGuardaConIdCero() {
        coEvery {
            repository.registrarTemperaturaEnServidor(any(), any(), any(), any())
        } throws RuntimeException("Sin conexión")
        val guardada = slot<LineBebedero>()
        coEvery { repository.guardar(capture(guardada)) } just Runs
        val viewModel = BebederoViewModel(repository)

        viewModel.guardarTemperatura("Granja Santiago", "Galpón 4", "Línea 4", 23.0)

        // id 0 = Room le asigna uno nuevo
        assertEquals(0, guardada.captured.id)
        assertEquals("Granja Santiago", guardada.captured.granja)
    }
}
