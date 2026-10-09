package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.miapp_grupox.model.LineBebedero
import com.example.miapp_grupox.model.OpcionesBebedero
import com.example.miapp_grupox.model.RangosTemperatura
import com.example.miapp_grupox.model.vibrarConfirmacion
import kotlinx.coroutines.launch

@Composable
fun TemperaturasScreen(
    lineas: List<LineBebedero>,
    onGuardar: (String, String, String, Double) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    // Estado y corrutina para mostrar mensajes al pie de la pantalla (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var granja by remember { mutableStateOf(OpcionesBebedero.granjas[0]) }
    var galpon by remember { mutableStateOf(OpcionesBebedero.galpones[0]) }
    var linea by remember { mutableStateOf(OpcionesBebedero.lineas[0]) }
    var rango by remember { mutableStateOf("") }
    var hayError by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AppBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AriztiaHeader(
                "Control de temperaturas",
                "Mediciones por granja, galpón y línea"
            )

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AriztiaSectionTitle("Registrar medición")
                }

                item {
                    AriztiaWhiteCard {
                        AriztiaDropdown(
                            label = "Granja",
                            opciones = OpcionesBebedero.granjas,
                            seleccion = granja,
                            onSeleccion = { granja = it }
                        )

                        Spacer(Modifier.height(8.dp))

                        AriztiaDropdown(
                            label = "Galpón",
                            opciones = OpcionesBebedero.galpones,
                            seleccion = galpon,
                            onSeleccion = { galpon = it }
                        )

                        Spacer(Modifier.height(8.dp))

                        AriztiaDropdown(
                            label = "Línea",
                            opciones = OpcionesBebedero.lineas,
                            seleccion = linea,
                            onSeleccion = { linea = it }
                        )

                        Spacer(Modifier.height(8.dp))

                        AriztiaDropdown(
                            label = "Rango de temperatura",
                            opciones = RangosTemperatura.etiquetas,
                            seleccion = rango,
                            onSeleccion = {
                                rango = it
                                hayError = false
                            },
                            isError = hayError
                        )

                        Spacer(Modifier.height(12.dp))

                        AriztiaLightButton(
                            texto = "Guardar temperatura",
                            onClick = {
                                val valor = RangosTemperatura.valorDe(rango)

                                if (valor == null) {
                                    hayError = true
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Selecciona un rango de temperatura"
                                        )
                                    }
                                } else {
                                    onGuardar(granja, galpon, linea, valor)

                                    // Vibración como confirmación táctil al guardar
                                    vibrarConfirmacion(context)

                                    rango = ""
                                    hayError = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Temperatura registrada correctamente"
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                item {
                    AriztiaSectionTitle(
                        "Líneas registradas",
                        "${lineas.size} líneas en la base de datos"
                    )
                }

                items(lineas, key = { it.id }) { item ->
                    TarjetaLinea(item)
                }

                item {
                    AriztiaSecondaryButton("Volver", onVolver)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TarjetaLinea(linea: LineBebedero) {
    val color = when (linea.estado) {
        "Crítico" -> EstadoCritico
        "Advertencia" -> EstadoAdvertenciaTexto
        else -> EstadoNormal
    }

    AriztiaWhiteCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    linea.granja,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "${linea.galpon} · ${linea.linea}",
                    color = TextSecondary
                )
            }
            EstadoBadge(linea.estado)
        }

        Spacer(Modifier.height(12.dp))

        Text("Rango de temperatura", color = TextSecondary)

        Text(
            RangosTemperatura.etiquetaDe(linea.temperatura),
            style = MaterialTheme.typography.headlineSmall,
            color = color
        )

        Text("Última medición: ${linea.fecha}", color = TextSecondary)
    }
}