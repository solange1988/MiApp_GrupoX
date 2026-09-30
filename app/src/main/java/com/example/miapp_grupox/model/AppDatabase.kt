package com.example.miapp_grupox.model


import androidx.room.Database
import androidx.room.RoomDatabase

// Define la base de datos completa de la app: qué tablas tiene y su versión
@Database(entities = [FlushingEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flushingDao(): FlushingDao
}