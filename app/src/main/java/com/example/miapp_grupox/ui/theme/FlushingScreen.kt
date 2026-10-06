
package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miapp_grupox.model.FlusingEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FlusingScreen(
    registros: List<FlusingEntity>,
    onGuardar: (FlusingEntity) -> Unit,
    onVolver: () -> Unit
) {
    var granja by remember { mutableStateOf("Granja Melipilla") }
    var galpon by remember { mutableStateOf("Galpón 1") }
    var linea by remember { mutableStateOf("Línea 1") }
    var responsable by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("Limpieza preventiva") }
    var observaciones by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        AriztiaHeader(
            "Registro de flushing",
            "Control de limpieza de líneas de bebederos"
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AriztiaSectionTitle(
                    "Nuevo registro",
                    "Completa los datos de la actividad realizada."
                )
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
                        label = { Text("Línea de bebederos") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = responsable,
                        onValueChange = { responsable = it },
                        label = { Text("Responsable") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Motivo del flushing",
                        style = MaterialTheme.typography.titleMedium
                    )

                    listOf(
                        "Limpieza preventiva",
                        "Temperatura elevada",
                        "Mantenimiento",
                        "Alerta sanitaria",
                        "Otro"
                    ).forEach { opcion ->
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            RadioButton(
                                selected = motivo == opcion,
                                onClick = { motivo = opcion }
                            )
                            Text(opcion)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = observaciones,
                        onValueChange = { observaciones = it },
                        label = { Text("Observaciones") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Fecha y hora: ${fechaActual()}",
                        color = TextSecondary
                    )

                    if (mensaje.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(mensaje, color = AriztiaRed)
                    }

                    Spacer(Modifier.height(12.dp))


                    AriztiaPrimaryButton(
                        texto = "Guardar registro",
                        onClick = {
                            if (
                                granja.isBlank() ||
                                galpon.isBlank() ||
                                linea.isBlank() ||
                                responsable.isBlank()
                            ) {
                                mensaje = "Completa granja, galpón, línea y responsable."
                            } else {
                                val registro = FlusingEntity(
                                    granja = granja.trim(),
                                    galpon = galpon.trim(),
                                    linea = linea.trim(),
                                    responsable = responsable.trim(),
                                    motivo = motivo,
                                    observaciones = observaciones.trim(),
                                    fecha = fechaActual()
                                )

                                onGuardar(registro)
                                responsable = ""
                                observaciones = ""
                                mensaje = "Registro enviado para guardar."
                            }
                        }
                    )
                }
            }

            item {
                AriztiaSectionTitle(
                    "Historial de flushing",
                    "${registros.size} registros guardados"
                )
            }

            if (registros.isEmpty()) {
                item {
                    AriztiaWhiteCard {
                        Text("Sin registros", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Los registros que guardes aparecerán aquí.",
                            color = TextSecondary
                        )
                    }
                }
            }

            items(registros, key = { it.id }) { registro ->
                AriztiaWhiteCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${registro.granja} · ${registro.galpon}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(registro.linea, color = AriztiaRed)
                        }

                        EstadoBadge("Registrado")
                    }

                    HorizontalDivider(Modifier.padding(vertical = 10.dp))

                    TextoRegistro("Responsable", registro.responsable)
                    TextoRegistro("Motivo", registro.motivo)
                    TextoRegistro("Fecha", registro.fecha)

                    if (registro.observaciones.isNotBlank()) {
                        TextoRegistro("Observaciones", registro.observaciones)
                    }
                }
            }

            item {
                AriztiaSecondaryButton("Volver", onVolver)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TextoRegistro(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text("$etiqueta: ", color = TextPrimary)
        Text(valor, color = TextSecondary)
    }
}

private fun fechaActual(): String {
    return SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    ).format(Date())
}