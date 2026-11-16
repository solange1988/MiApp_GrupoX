package com.example.miapp_grupox.repository

import com.example.miapp_grupox.data.remote.api.InstalacionesApi
import com.example.miapp_grupox.data.remote.api.MonitoreoApi
import com.example.miapp_grupox.data.remote.dto.AlertaDto
import com.example.miapp_grupox.data.remote.dto.LineaDto
import com.example.miapp_grupox.model.LineBebedero
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AlertasRepositoryTest {

    private lateinit var monitoreoApi: MonitoreoApi
    private lateinit var instalacionesApi: InstalacionesApi
    private lateinit var bebederoRepository: BebederoRepository
    private lateinit var repo: AlertasRepository

    @Before
    fun preparar() {
        monitoreoApi = mockk()
        instalacionesApi = mockk()
        bebederoRepository = mockk()
        repo = AlertasRepository(monitoreoApi, instalacionesApi, bebederoRepository)
    }

    @Test
    fun alertasRemotas_agreganNombreDeGranjaGalponYLinea() = runTest {
        coEvery { instalacionesApi.listar(any(), any()) } returns listOf(
            LineaDto(id = 3L, granja = "Granja Rancagua", galpon = "Galpón 1", linea = "Línea 3")
        )
        coEvery { monitoreoApi.alertas(any()) } returns listOf(
            AlertaDto(
                medicionId = 5L, lineaId = 3L, nivel = "Crítico", temperatura = 32.0,
                mensaje = "Línea 3 en estado Crítico", fechaHora = "2026-11-02T09:00:00"
            )
        )

        val alertas = repo.obtenerAlertasRemotas()

        assertEquals(1, alertas.size)
        assertEquals("Granja Rancagua", alertas[0].granja)
        assertEquals("Galpón 1", alertas[0].galpon)
        assertEquals("Línea 3", alertas[0].linea)
        assertEquals("Crítico", alertas[0].nivel)
        assertEquals("02/11/2026 09:00", alertas[0].fecha)
    }

    @Test
    fun alertasRemotas_lineaDesconocida_usaValoresPorDefecto() = runTest {
        coEvery { instalacionesApi.listar(any(), any()) } returns emptyList()
        coEvery { monitoreoApi.alertas(any()) } returns listOf(
            AlertaDto(
                medicionId = null, lineaId = 99L, nivel = "Advertencia", temperatura = 28.5,
                mensaje = "", fechaHora = "2026-11-01T08:30:00"
            )
        )

        val alerta = repo.obtenerAlertasRemotas().first()

        assertEquals(0L, alerta.id)
        assertEquals("Sin granja", alerta.granja)
        assertEquals("Línea 99", alerta.linea)
    }

    @Test
    fun alertasLocales_soloIncluyenLineasQueNoEstanNormales() = runTest {
        every { bebederoRepository.obtenerLineas() } returns flowOf(
            listOf(
                LineBebedero(1, "Granja Melipilla", "Galpón 1", "Línea 1", 22.5, "demo"),
                LineBebedero(2, "Granja Melipilla", "Galpón 1", "Línea 2", 28.0, "demo"),
                LineBebedero(3, "Granja Melipilla", "Galpón 2", "Línea 1", 31.5, "demo")
            )
        )

        val alertas = repo.obtenerAlertasLocales()

        assertEquals(2, alertas.size)
        assertEquals("Advertencia", alertas[0].nivel)
        assertEquals("Crítico", alertas[1].nivel)
    }

    @Test
    fun formatearFecha_convierteFormatoIso() {
        assertEquals("02/11/2026 09:00", repo.formatearFecha("2026-11-02T09:00:00"))
    }

    @Test
    fun formatearFecha_textoInvalido_loDevuelveIgual() {
        assertEquals("sin-formato", repo.formatearFecha("sin-formato"))
    }
}
