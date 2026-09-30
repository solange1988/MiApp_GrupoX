package com.example.miapp_grupox.model


import androidx.room.Entity
import androidx.room.PrimaryKey

// Representa un registro de flushing guardado en la base de datos local
@Entity(tableName = "flushing_registros")
data class FlushingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreLinea: String,
    val observacion: String,
    val fecha: String
)