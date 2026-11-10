package com.example.miapp_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.miapp_grupox.viewmodel.AlertasViewModel
import com.example.miapp_grupox.viewmodel.BebederoViewModel
import com.example.miapp_grupox.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    // Líneas y temperaturas: sincroniza ms-instalaciones + ms-monitoreo con Room
    private val bebederoViewModel: BebederoViewModel by viewModels {
        BebederoViewModel.factory(application)
    }

    // Flushing (ms-flushing)
    private val mainViewModel: MainViewModel by viewModels()

    // Alertas desde ms-monitoreo (con respaldo en Room si no hay conexión)
    private val alertasViewModel: AlertasViewModel by viewModels {
        AlertasViewModel.factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MiAppGrupoXTheme {
                val lineas by bebederoViewModel.lineas
                    .collectAsStateWithLifecycle()

                // Aviso cuando no hay conexión con el servidor (null = todo bien)
                val avisoTemperaturas by bebederoViewModel.aviso
                    .collectAsStateWithLifecycle()

                val registros by mainViewModel.registros
                    .collectAsStateWithLifecycle()

                var pantalla by rememberSaveable { mutableStateOf("inicio") }

                // Perfil con el que se ingresó: define a qué panel se vuelve
                var perfil by rememberSaveable { mutableStateOf("operario") }

                // Botón "atrás" del celular: vuelve a la pantalla anterior
                BackHandler(enabled = pantalla != "inicio") {
                    pantalla = when (pantalla) {
                        "login" -> "inicio"
                        "roles" -> "login"
                        "operario", "supervisor" -> "roles"
                        else -> perfil // temperaturas, alertas y flushing
                    }
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
                        onOperario = {
                            perfil = "operario"
                            pantalla = "operario"
                        },
                        onSupervisor = {
                            perfil = "supervisor"
                            pantalla = "supervisor"
                        }
                    )

                    "operario" -> OperarioScreen(
                        onTemperaturas = { pantalla = "temperaturas" },
                        onFlushing = { pantalla = "flushing" },
                        onAlertas = { pantalla = "alertas" },
                        onInicio = { pantalla = "inicio" }
                    )

                    "supervisor" -> {
                        // Refresca el resumen con los datos del servidor al entrar
                        LaunchedEffect(Unit) {
                            bebederoViewModel.sincronizar()
                        }

                        SupervisorScreen(
                            lineas = lineas,
                            registros = registros,
                            onTemperaturas = { pantalla = "temperaturas" },
                            onAlertas = { pantalla = "alertas" },
                            onFlushing = { pantalla = "flushing" },
                            onInicio = { pantalla = "inicio" }
                        )
                    }

                    "temperaturas" -> {
                        // Refresca las líneas con los datos del servidor al entrar
                        LaunchedEffect(Unit) {
                            bebederoViewModel.sincronizar()
                        }

                        TemperaturasScreen(
                            lineas = lineas,
                            onGuardar = { granja, galpon, linea, temperatura ->
                                bebederoViewModel.guardarTemperatura(
                                    granja,
                                    galpon,
                                    linea,
                                    temperatura
                                )
                            },
                            onVolver = { pantalla = perfil },
                            aviso = avisoTemperaturas
                        )
                    }

                    "alertas" -> {
                        val estadoAlertas by alertasViewModel.estado
                            .collectAsStateWithLifecycle()

                        // Pide las alertas al servidor cada vez que se abre la pantalla
                        LaunchedEffect(Unit) {
                            alertasViewModel.cargarAlertas()
                        }

                        AlertasScreen(
                            estado = estadoAlertas,
                            onReintentar = { alertasViewModel.cargarAlertas() },
                            onVolver = { pantalla = perfil }
                        )
                    }

                    "flushing" -> FlusingScreen(
                        registros = registros,
                        onGuardar = { registro ->
                            mainViewModel.guardarRegistro(registro)
                        },
                        onEliminar = { id ->
                            mainViewModel.eliminarRegistro(id)
                        },
                        onActualizarObservacion = { registro, nuevaObservacion ->
                            mainViewModel.actualizarObservacion(registro, nuevaObservacion)
                        },
                        onVolver = { pantalla = perfil }
                    )

                    else -> HomeScreen {
                        pantalla = "login"
                    }
                }
            }
        }
    }
}