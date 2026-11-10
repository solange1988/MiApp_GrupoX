package com.example.miapp_grupox.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlusingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registro: FlusingEntity)

    // NUEVO: inserta varios registros de una vez (al sincronizar con el servicio)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(registros: List<FlusingEntity>)

    @Query("SELECT * FROM registros_flushing ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<FlusingEntity>>

    // Modifica un registro existente (se identifica por su id)
    @Update
    suspend fun actualizar(registro: FlusingEntity)

    @Query("DELETE FROM registros_flushing WHERE id = :id")
    suspend fun eliminar(id: Int)

    // NUEVO: borra todo antes de cargar lo que viene del servicio
    @Query("DELETE FROM registros_flushing")
    suspend fun eliminarTodos()

    @Query("SELECT COUNT(*) FROM registros_flushing")
    suspend fun contar(): Int
}