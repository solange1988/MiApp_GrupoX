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
import com.example.miapp_grupox.ui.theme.AlertasScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                        SupervisorScreen()
                    }
                    composable("temperaturas") {
                        TemperaturasScreen()
                    }
                    composable("alertas") {
                        AlertasScreen()
                    }
                }
            }
        }
    }
}
