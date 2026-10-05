
package com.example.miapp_grupox.repository

import com.example.miapp_grupox.model.BebederoDao
import com.example.miapp_grupox.model.LineBebedero
import kotlinx.coroutines.flow.Flow

class BebederoRepository(
    private val dao: BebederoDao
) {
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
}