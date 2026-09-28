package com.example.miapp_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miapp_grupox.ui.theme.MiApp_GrupoXTheme
import com.example.miapp_grupox.ui.theme.RolSelectionScreen
import com.example.miapp_grupox.ui.theme.OperarioScreen
import com.example.miapp_grupox.ui.theme.SupervisorScreen
import com.example.miapp_grupox.ui.theme.TemperaturasScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiApp_GrupoXTheme {
                // Controlador de navegación: administra el cambio entre pantallas
                val navController = rememberNavController()

                // NavHost define todas las "rutas" posibles de la app
                NavHost(
                    navController = navController,
                    startDestination = "roles"  // pantalla inicial
                ) {
                    composable("roles") {
                        RolSelectionScreen(navController = navController)
                    }
                    composable("operario") {
                        OperarioScreen(navController = navController)
                    }
                    composable("supervisor") {
                        SupervisorScreen()
                    }
                    composable("temperaturas") {
                        TemperaturasScreen()
                    }
                }
            }
        }
    }
}