package com.example.miapp_grupox.model


import android.content.Context
import androidx.room.Room

// Guarda una única instancia de la base de datos para toda la app
object DatabaseProvider {
    private var instancia: AppDatabase? = null

    fun obtenerBaseDeDatos(context: Context): AppDatabase {
        if (instancia == null) {
            instancia = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
        }
        return instancia!!
    }
}