package com.example.miapp_grupox.model


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// Define las operaciones permitidas sobre la tabla de flushing
@Dao
interface FlushingDao {

    // Inserta un nuevo registro de flushing
    @Insert
    suspend fun insertar(flushing: FlushingEntity)

    // Trae todos los registros guardados, ordenados del más nuevo al más viejo
    @Query("SELECT * FROM flushing_registros ORDER BY id DESC")
    suspend fun obtenerTodos(): List<FlushingEntity>
}