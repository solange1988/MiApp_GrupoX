package com.example.miapp_grupox.repository

import android.util.Log
import com.example.miapp_grupox.api.FlushingApi
import com.example.miapp_grupox.api.RetrofitClient
import com.example.miapp_grupox.api.toDto
import com.example.miapp_grupox.api.toEntity
import com.example.miapp_grupox.model.FlusingDao
import com.example.miapp_grupox.model.FlusingEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class MainRepository(
    private val dao: FlusingDao,
    private val api: FlushingApi = RetrofitClient.flushingApi
) {
    private val tag = "MainRepository"

    // La pantalla sigue leyendo desde Room, como antes
    fun obtenerRegistros(): Flow<List<FlusingEntity>> =
        dao.obtenerTodos()

    // Trae los registros de ms-flushing y reemplaza la copia local
    suspend fun sincronizar(): Boolean {
        return try {
            val remotos = api.listar().map { it.toEntity() }
            dao.eliminarTodos()
            dao.insertarTodos(remotos)
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(tag, "No se pudo sincronizar: ${e.message}")
            false
        }
    }

    suspend fun guardarRegistro(registro: FlusingEntity): Boolean {
        return try {
            val guardado = api.crear(registro.toDto()).toEntity()
            dao.insertar(guardado)
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(tag, "No se pudo guardar: ${e.message}")
            false
        }
    }

    suspend fun actualizarRegistro(registro: FlusingEntity): Boolean {
        return try {
            val actualizado = api.actualizar(registro.id.toLong(), registro.toDto()).toEntity()
            dao.actualizar(actualizado)
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(tag, "No se pudo actualizar: ${e.message}")
            false
        }
    }

    suspend fun eliminarRegistro(id: Int): Boolean {
        return try {
            val respuesta = api.eliminar(id.toLong())
            if (respuesta.isSuccessful) {
                dao.eliminar(id)
                true
            } else {
                false
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(tag, "No se pudo eliminar: ${e.message}")
            false
        }
    }
}