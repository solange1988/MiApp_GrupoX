
package com.example.miapp_grupox.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BebederoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(linea: LineBebedero)

    @Query("SELECT * FROM lineas_bebedero ORDER BY granja, galpon, linea")
    fun obtenerTodas(): Flow<List<LineBebedero>>

    @Query("SELECT COUNT(*) FROM lineas_bebedero")
    suspend fun contar(): Int

    @Query("SELECT * FROM lineas_bebedero WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): LineBebedero?
}