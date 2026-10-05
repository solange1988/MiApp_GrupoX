
package com.example.miapp_grupox.repository

import com.example.miapp_grupox.model.FlusingDao
import com.example.miapp_grupox.model.FlusingEntity
import kotlinx.coroutines.flow.Flow

class MainRepository(
    private val dao: FlusingDao
) {
    fun obtenerRegistros(): Flow<List<FlusingEntity>> =
        dao.obtenerTodos()

    suspend fun guardarRegistro(registro: FlusingEntity) =
        dao.insertar(registro)

    suspend fun eliminarRegistro(id: Int) =
        dao.eliminar(id)
}