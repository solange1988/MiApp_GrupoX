
package com.example.miapp_grupox.repository

import com.example.miapp_grupox.data.remote.RetrofitClient
import com.example.miapp_grupox.data.remote.api.InstalacionesApi
import com.example.miapp_grupox.data.remote.api.MonitoreoApi
import com.example.miapp_grupox.data.remote.dto.LineaDto
import com.example.miapp_grupox.data.remote.dto.MedicionDto
import com.example.miapp_grupox.model.BebederoDao
import com.example.miapp_grupox.model.LineBebedero
import kotlinx.coroutines.flow.Flow

class BebederoRepository(
    private val dao: BebederoDao,
    private val instalacionesApi: InstalacionesApi = RetrofitClient.instalacionesApi,
    private val monitoreoApi: MonitoreoApi = RetrofitClient.monitoreoApi
) {

    // ---------- Room (caché local) ----------

    fun obtenerLineas(): Flow<List<LineBebedero>> =
        dao.obtenerTodas()

    suspend fun guardar(linea: LineBebedero) =
        dao.guardar(linea)

    suspend fun inicializarDatos() {
        if (dao.contar() == 0) {
            dao.guardar(
                LineBebedero(
                    granja = "Granja Melipilla",
                    galpon = "Galpón 1",
                    linea = "Línea 1",
                    temperatura = 22.5,
                    fecha = "Datos de demostración"
                )
            )
            dao.guardar(
                LineBebedero(
                    granja = "Granja Melipilla",
                    galpon = "Galpón 1",
                    linea = "Línea 2",
                    temperatura = 28.0,
                    fecha = "Datos de demostración"
                )
            )
            dao.guardar(
                LineBebedero(
                    granja = "Granja Melipilla",
                    galpon = "Galpón 2",
                    linea = "Línea 1",
                    temperatura = 31.5,
                    fecha = "Datos de demostración"
                )
            )
        }
    }

    // ---------- Servidor (microservicios) ----------

    // Trae líneas (ms-instalaciones) y su última medición (ms-monitoreo) y actualiza Room
    suspend fun sincronizarConServidor() {
        val lineas = instalacionesApi.listar()
        val mediciones = monitoreoApi.listarMediciones()

        // Última medición de cada línea
        val ultimaPorLinea = mediciones
            .groupBy { it.lineaId }
            .mapValues { (_, lista) -> lista.maxByOrNull { it.fechaHora ?: "" } }

        val locales = lineas.mapNotNull { linea ->
            val ultima = linea.id?.let { ultimaPorLinea[it] } ?: return@mapNotNull null
            LineBebedero(
                id = (linea.id ?: 0L).toInt(),
                granja = linea.granja,
                galpon = linea.galpon,
                linea = linea.linea,
                temperatura = ultima.temperatura,
                fecha = formatearFecha(ultima.fechaHora)
            )
        }

        dao.borrarTodas()
        dao.guardarTodas(locales)
    }

    // Registra una medición: busca (o crea) la línea en ms-instalaciones y envía la medición a ms-monitoreo
    suspend fun registrarTemperaturaEnServidor(
        granja: String,
        galpon: String,
        linea: String,
        temperatura: Double
    ) {
        val existente = instalacionesApi.listar(granja, galpon)
            .firstOrNull { it.linea == linea }

        val lineaId = existente?.id
            ?: instalacionesApi.crear(LineaDto(granja = granja, galpon = galpon, linea = linea)).id
            ?: throw IllegalStateException("El servidor no devolvió el id de la línea")

        monitoreoApi.crearMedicion(
            MedicionDto(lineaId = lineaId, temperatura = temperatura)
        )
    }

    // "2026-11-02T09:00:00" -> "02/11/2026 09:00"
    fun formatearFecha(iso: String?): String {
        if (iso == null) return "Sin fecha"
        return try {
            val partes = iso.split("T")
            val f = partes[0].split("-")
            "${f[2]}/${f[1]}/${f[0]} ${partes[1].take(5)}"
        } catch (e: Exception) {
            iso
        }
    }
}