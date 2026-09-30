package com.example.miapp_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miapp_grupox.model.DatabaseProvider
import com.example.miapp_grupox.ui.theme.MiApp_GrupoXTheme
import com.example.miapp_grupox.ui.theme.RolSelectionScreen
import com.example.miapp_grupox.ui.theme.OperarioScreen
import com.example.miapp_grupox.ui.theme.SupervisorScreen
import com.example.miapp_grupox.ui.theme.TemperaturasScreen
import com.example.miapp_grupox.ui.theme.AlertasScreen
import com.example.miapp_grupox.ui.theme.FlushingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa la base de datos una sola vez, al arrancar la app
        DatabaseProvider.obtenerBaseDeDatos(applicationContext)

        setContent {
            MiApp_GrupoXTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "roles"
                ) {
                    composable("roles") {
                        RolSelectionScreen(navController = navController)
                    }
                    composable("operario") {
                        OperarioScreen(navController = navController)
                    }
                    composable("supervisor") {
                        SupervisorScreen(navController = navController)
                    }
                    composable("temperaturas") {
                        TemperaturasScreen(navController = navController)
                    }
                    composable("alertas") {
                        AlertasScreen(navController = navController)
                    }
                    composable("flushing") {
                        FlushingScreen(navController = navController)
                    }
                }
            }
        }
    }
}