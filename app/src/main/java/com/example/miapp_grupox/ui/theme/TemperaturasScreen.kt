
package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.miapp_grupox.model.LineBebedero

@Composable
fun TemperaturasScreen(
    lineas: List<LineBebedero>,
    onGuardar: (String, String, String, Double) -> Unit,
    onVolver: () -> Unit
) {
    var granja by remember { mutableStateOf("Granja Melipilla") }
    var galpon by remember { mutableStateOf("Galpón 1") }
    var linea by remember { mutableStateOf("Línea 1") }
    var temperatura by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
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
                    OutlinedTextField(
                        value = granja,
                        onValueChange = { granja = it },
                        label = { Text("Granja") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = galpon,
                        onValueChange = { galpon = it },
                        label = { Text("Galpón") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = linea,
                        onValueChange = { linea = it },
                        label = { Text("Línea") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = temperatura,
                        onValueChange = { temperatura = it },
                        label = { Text("Temperatura (°C)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (mensaje.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(mensaje, color = AriztiaRed)
                    }

                    Spacer(Modifier.height(12.dp))


                    AriztiaPrimaryButton(
                        texto = "Guardar temperatura",
                        onClick = {
                            val valor = temperatura
                                .replace(",", ".")
                                .toDoubleOrNull()

                            when {
                                granja.isBlank() ||
                                        galpon.isBlank() ||
                                        linea.isBlank() -> {
                                    mensaje = "Completa todos los campos."
                                }

                                valor == null || valor !in 0.0..100.0 -> {
                                    mensaje = "Ingresa una temperatura entre 0 y 100 °C."
                                }

                                else -> {
                                    onGuardar(
                                        granja.trim(),
                                        galpon.trim(),
                                        linea.trim(),
                                        valor
                                    )

                                    temperatura = ""
                                    mensaje = "Medición enviada para guardar."
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

        Text(
            "${String.format(java.util.Locale.getDefault(), "%.1f", linea.temperatura)} °C",
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )

        Text("Última medición: ${linea.fecha}", color = TextSecondary)
    }
}