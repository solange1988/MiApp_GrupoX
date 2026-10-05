
package com.example.miapp_grupox.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FlusingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registro: FlusingEntity)

    @Query("SELECT * FROM registros_flushing ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<FlusingEntity>>

    @Query("DELETE FROM registros_flushing WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Query("SELECT COUNT(*) FROM registros_flushing")
    suspend fun contar(): Int
}