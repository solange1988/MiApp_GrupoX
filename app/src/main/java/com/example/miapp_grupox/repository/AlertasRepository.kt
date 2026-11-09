package com.example.miapp_grupox.repository

import com.example.miapp_grupox.data.remote.api.InstalacionesApi
import com.example.miapp_grupox.data.remote.api.MonitoreoApi
import com.example.miapp_grupox.model.AlertaUi
import kotlinx.coroutines.flow.first

class AlertasRepository(
    private val monitoreoApi: MonitoreoApi,
    private val instalacionesApi: InstalacionesApi,
    private val bebederoRepository: BebederoRepository
) {

    // Alertas desde ms-monitoreo, con nombres de granja/galpón/línea desde ms-instalaciones
    suspend fun obtenerAlertasRemotas(): List<AlertaUi> {
        val lineasPorId = instalacionesApi.listar().associateBy { it.id }

        return monitoreoApi.alertas().map { alerta ->
            val linea = lineasPorId[alerta.lineaId]
            AlertaUi(
                id = alerta.medicionId ?: 0L,
                granja = linea?.granja ?: "Sin granja",
                galpon = linea?.galpon ?: "-",
                linea = linea?.linea ?: "Línea ${alerta.lineaId}",
                nivel = alerta.nivel,
                temperatura = alerta.temperatura,
                fecha = formatearFecha(alerta.fechaHora)
            )
        }
    }

    // Respaldo sin conexión: alertas calculadas con lo guardado en Room
    suspend fun obtenerAlertasLocales(): List<AlertaUi> =
        bebederoRepository.obtenerLineas().first()
            .filter { it.estado != "Normal" }
            .map {
                AlertaUi(
                    id = it.id.toLong(),
                    granja = it.granja,
                    galpon = it.galpon,
                    linea = it.linea,
                    nivel = it.estado,
                    temperatura = it.temperatura,
                    fecha = it.fecha
                )
            }

    // "2026-11-02T09:00:00" -> "02/11/2026 09:00"
    fun formatearFecha(iso: String): String = try {
        val partes = iso.split("T")
        val f = partes[0].split("-")
        "${f[2]}/${f[1]}/${f[0]} ${partes[1].take(5)}"
    } catch (e: Exception) {
        iso
    }
}