package com.example.miapp_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.miapp_grupox.ui.theme.AlertasScreen
import com.example.miapp_grupox.ui.theme.FlusingScreen
import com.example.miapp_grupox.ui.theme.HomeScreen
import com.example.miapp_grupox.ui.theme.LoginScreen
import com.example.miapp_grupox.ui.theme.MiAppGrupoXTheme
import com.example.miapp_grupox.ui.theme.OperarioScreen
import com.example.miapp_grupox.ui.theme.RolSelectionScreen
import com.example.miapp_grupox.ui.theme.SupervisorScreen
import com.example.miapp_grupox.ui.theme.TemperaturasScreen
import com.example.miapp_grupox.viewmodel.BebederoViewModel
import com.example.miapp_grupox.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val bebederoViewModel: BebederoViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MiAppGrupoXTheme {
                val lineas by bebederoViewModel.lineas
                    .collectAsStateWithLifecycle()

                val registros by mainViewModel.registros
                    .collectAsStateWithLifecycle()

                var pantalla by remember {
                    mutableStateOf("inicio")
                }

                when (pantalla) {
                    "inicio" -> HomeScreen {
                        pantalla = "login"
                    }

                    "login" -> LoginScreen(
                        onLoginExitoso = { pantalla = "roles" },
                        onVolver = { pantalla = "inicio" }
                    )

                    "roles" -> RolSelectionScreen(
                        onOperario = { pantalla = "operario" },
                        onSupervisor = { pantalla = "supervisor" }
                    )

                    "operario" -> OperarioScreen(
                        onTemperaturas = { pantalla = "temperaturas" },
                        onFlushing = { pantalla = "flushing" },
                        onAlertas = { pantalla = "alertas" },
                        onInicio = { pantalla = "inicio" }
                    )

                    "supervisor" -> SupervisorScreen(
                        lineas = lineas,
                        registros = registros,
                        onTemperaturas = { pantalla = "temperaturas" },
                        onAlertas = { pantalla = "alertas" },
                        onFlushing = { pantalla = "flushing" },
                        onInicio = { pantalla = "inicio" }
                    )

                    "temperaturas" -> TemperaturasScreen(
                        lineas = lineas,
                        onGuardar = { granja, galpon, linea, temperatura ->
                            bebederoViewModel.guardarTemperatura(
                                granja,
                                galpon,
                                linea,
                                temperatura
                            )
                        },
                        onVolver = {
                            pantalla = "operario"
                        }
                    )

                    "alertas" -> AlertasScreen(
                        lineas = lineas,
                        onVolver = {
                            pantalla = "operario"
                        }
                    )

                    "flushing" -> FlusingScreen(
                        registros = registros,
                        onGuardar = { registro ->
                            mainViewModel.guardarRegistro(registro)
                        },
                        onVolver = {
                            pantalla = "operario"
                        }
                    )

                    else -> HomeScreen {
                        pantalla = "login"
                    }
                }
            }
        }
    }
}