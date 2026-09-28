package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miapp_grupox.viewmodel.BebederoViewModel

@Composable
fun TemperaturasScreen(viewModel: BebederoViewModel = viewModel()) {
    val lineas = viewModel.obtenerLineas()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Temperaturas",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        lineas.forEach { linea ->
            val colorEstado = when (linea.estado) {
                "Normal" -> Color(0xFF2E7D32)
                "Advertencia" -> Color(0xFFFFA500)
                else -> Color(0xFFC62828)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Granja: ${linea.granja}")
                    Text(text = "Galpón: ${linea.galpon}")
                    Text(text = "Línea: ${linea.nombre}")
                    Text(text = "Temperatura: ${linea.temperatura} °C")
                    Text(
                        text = "Estado: ${linea.estado}",
                        color = colorEstado
                    )
                }
            }
        }
    }
}