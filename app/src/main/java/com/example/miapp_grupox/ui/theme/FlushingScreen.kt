package com.example.miapp_grupox.ui.theme


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miapp_grupox.repository.BebederoRepository

// Pantalla para registrar que se realizó un flushing en una línea específica
@Composable
fun FlushingScreen() {
    val repository = BebederoRepository()
    val lineas = repository.obtenerLineas()

    // Estado del formulario: qué línea se eligió y si ya se registró
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

        // Un botón por cada línea disponible (forma simple de "seleccionar")
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

        // Campo de texto para la observación
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

        // Mensaje de error si falta completar algo
        if (error) {
            Text(
                text = "Debe seleccionar una línea y escribir una observación.",
                color = androidx.compose.ui.graphics.Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para confirmar el registro
        Button(
            onClick = {
                if (lineaSeleccionada.isEmpty() || observacion.isBlank()) {
                    error = true
                    registroExitoso = false
                } else {
                    error = false
                    registroExitoso = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar Flushing")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mensaje de confirmación visual
        if (registroExitoso) {
            Text(
                text = "✅ Flushing registrado en $lineaSeleccionada",
                color = androidx.compose.ui.graphics.Color(0xFF2E7D32)
            )
        }
    }
}