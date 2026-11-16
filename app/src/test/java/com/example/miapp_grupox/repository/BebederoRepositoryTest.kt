package com.example.miapp_grupox.repository

import com.example.miapp_grupox.data.remote.api.InstalacionesApi
import com.example.miapp_grupox.data.remote.api.MonitoreoApi
import com.example.miapp_grupox.data.remote.dto.LineaDto
import com.example.miapp_grupox.data.remote.dto.MedicionDto
import com.example.miapp_grupox.model.BebederoDao
import com.example.miapp_grupox.model.LineBebedero
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BebederoRepositoryTest {

    private lateinit var dao: BebederoDao
    private lateinit var instalacionesApi: InstalacionesApi
    private lateinit var monitoreoApi: MonitoreoApi
    private lateinit var repo: BebederoRepository

    @Before
    fun preparar() {
        dao = mockk(relaxed = true)
        instalacionesApi = mockk()
        monitoreoApi = mockk()
        repo = BebederoRepository(dao, instalacionesApi, monitoreoApi)
    }

    // ---------- datos iniciales ----------

    @Test
    fun inicializarDatos_roomVacio_guardaTresLineasDeDemostracion() = runTest {
        coEvery { dao.contar() } returns 0

        repo.inicializarDatos()

        coVerify(exactly = 3) { dao.guardar(any()) }
    }

    @Test
    fun inicializarDatos_roomConDatos_noGuardaNada() = runTest {
        coEvery { dao.contar() } returns 5

        repo.inicializarDatos()

        coVerify(exactly = 0) { dao.guardar(any()) }
    }

    // ---------- sincronizar con el servidor ----------

    @Test
    fun sincronizar_guardaCadaLineaConSuUltimaMedicion() = runTest {
        coEvery { instalacionesApi.listar(any(), any()) } returns listOf(
            LineaDto(1L, "Granja Melipilla", "Galpón 1", "Línea 1"),
            LineaDto(2L, "Granja Melipilla", "Galpón 2", "Línea 2"),
            LineaDto(3L, "Granja Rancagua", "Galpón 1", "Línea 3") // sin mediciones
        )
        coEvery { monitoreoApi.listarMediciones() } returns listOf(
            MedicionDto(1L, 1L, 24.5, "Normal", "2026-11-01T08:00:00"),
            MedicionDto(2L, 1L, 31.0, "Crítico", "2026-11-02T08:00:00"),
            MedicionDto(3L, 2L, 28.5, "Advertencia", "2026-11-01T08:30:00")
        )
        val guardadas = slot<List<LineBebedero>>()
        coEvery { dao.guardarTodas(capture(guardadas)) } returns Unit

        repo.sincronizarConServidor()

        // La línea 3 no tiene mediciones, así que no se guarda
        assertEquals(2, guardadas.captured.size)
        val linea1 = guardadas.captured.first { it.id == 1 }
        assertEquals(31.0, linea1.temperatura, 0.0) // la más reciente
        assertEquals("02/11/2026 08:00", linea1.fecha)
        coVerifyOrder {
            dao.borrarTodas()
            dao.guardarTodas(any())
        }
    }

    // ---------- registrar temperatura ----------

    @Test
    fun registrarTemperatura_lineaExistente_noCreaLineaNueva() = runTest {
        coEvery { instalacionesApi.listar("Granja Melipilla", "Galpón 1") } returns listOf(
            LineaDto(4L, "Granja Melipilla", "Galpón 1", "Línea 2")
        )
        coEvery { monitoreoApi.crearMedicion(any()) } returns MedicionDto(10L, 4L, 32.0)

        repo.registrarTemperaturaEnServidor("Granja Melipilla", "Galpón 1", "Línea 2", 32.0)

        coVerify(exactly = 0) { instalacionesApi.crear(any()) }
        coVerify { monitoreoApi.crearMedicion(match { it.lineaId == 4L && it.temperatura == 32.0 }) }
    }

    @Test
    fun registrarTemperatura_lineaNueva_laCreaYUsaSuId() = runTest {
        coEvery { instalacionesApi.listar("Granja Santiago", "Galpón 1") } returns emptyList()
        coEvery { instalacionesApi.crear(any()) } returns
                LineaDto(5L, "Granja Santiago", "Galpón 1", "Línea 1")
        coEvery { monitoreoApi.crearMedicion(any()) } returns MedicionDto(11L, 5L, 23.0)

        repo.registrarTemperaturaEnServidor("Granja Santiago", "Galpón 1", "Línea 1", 23.0)

        coVerify(exactly = 1) { instalacionesApi.crear(any()) }
        coVerify { monitoreoApi.crearMedicion(match { it.lineaId == 5L }) }
    }

    @Test
    fun registrarTemperatura_servidorSinId_lanzaError() = runTest {
        coEvery { instalacionesApi.listar(any(), any()) } returns emptyList()
        coEvery { instalacionesApi.crear(any()) } returns
                LineaDto(null, "Granja Santiago", "Galpón 1", "Línea 1")

        val error = runCatching {
            repo.registrarTemperaturaEnServidor("Granja Santiago", "Galpón 1", "Línea 1", 23.0)
        }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
    }

    // ---------- formato de fecha ----------

    @Test
    fun formatearFecha_iso_conviertePorDiaMesAnio() {
        assertEquals("02/11/2026 09:00", repo.formatearFecha("2026-11-02T09:00:00"))
    }

    @Test
    fun formatearFecha_nula_devuelveSinFecha() {
        assertEquals("Sin fecha", repo.formatearFecha(null))
    }
}
