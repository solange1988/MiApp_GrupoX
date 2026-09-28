package com.example.miapp_grupox.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miapp_grupox.viewmodel.BebederoViewModel

@Composable
fun FlushingScreen(viewModel: BebederoViewModel = viewModel()) {
    val lineas = viewModel.obtenerLineas()

    var lineaSeleccionada by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }
    var registroExitoso by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

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
                // La validación ahora vive en el ViewModel, no aquí
                val exito = viewModel.registrarFlushing(lineaSeleccionada, observacion)
                error = !exito
                registroExitoso = exito
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
    }
}