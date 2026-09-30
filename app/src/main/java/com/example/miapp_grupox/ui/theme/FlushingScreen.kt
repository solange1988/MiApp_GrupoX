package com.example.miapp_grupox.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.miapp_grupox.model.FlushingEntity
import com.example.miapp_grupox.viewmodel.BebederoViewModel

@Composable
fun FlushingScreen(navController: NavController, viewModel: BebederoViewModel = viewModel()) {
    val lineas = viewModel.obtenerLineas()

    var lineaSeleccionada by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }
    var registroExitoso by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var historial by remember { mutableStateOf<List<FlushingEntity>>(emptyList()) }

    // Función para recargar el historial desde la base de datos
    suspend fun recargarHistorial() {
        historial = viewModel.obtenerHistorialFlushing()
    }

    // Carga el historial la primera vez que se abre la pantalla
    LaunchedEffect(Unit) {
        recargarHistorial()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Registrar Flushing",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Seleccione la línea a registrar:")

        Spacer(modifier = Modifier.height(8.dp))

        lineas.forEach { linea ->
            Button(
                onClick = {
                    lineaSeleccionada = linea.nombre
                    error = false
                    registroExitoso = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("${linea.nombre} - ${linea.galpon}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = observacion,
            onValueChange = {
                observacion = it
                error = false
            },
            label = { Text("Observación") },
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )

        if (error) {
            Text(
                text = "Debe seleccionar una línea y escribir una observación.",
                color = androidx.compose.ui.graphics.Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.registrarFlushing(lineaSeleccionada, observacion) { exito ->
                    error = !exito
                    registroExitoso = exito
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar Flushing")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = registroExitoso) {
            Text(
                text = "✅ Flushing registrado en $lineaSeleccionada",
                color = androidx.compose.ui.graphics.Color(0xFF2E7D32)
            )
        }

        // Cuando se registra con éxito, recarga el historial para mostrarlo actualizado
        LaunchedEffect(registroExitoso) {
            if (registroExitoso) {
                recargarHistorial()
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Historial de Flushing",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (historial.isEmpty()) {
            Text(text = "Aún no hay registros guardados.")
        } else {
            historial.forEach { registro ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "${registro.nombreLinea} — ${registro.fecha}")
                        Text(text = registro.observacion)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}