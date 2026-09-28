package com.example.miapp_grupox.ui.theme



import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.miapp_grupox.repository.BebederoRepository

// Pantalla que muestra únicamente las líneas con alertas (Advertencia o Crítico)
@Composable
fun AlertasScreen() {
    val repository = BebederoRepository()
    val alertas = repository.obtenerAlertas()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Alertas Activas",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Si no hay alertas, mostramos un mensaje tranquilizador
        if (alertas.isEmpty()) {
            Text(text = "No hay alertas activas en este momento.")
        } else {
            alertas.forEach { linea ->
                val colorEstado = if (linea.estado == "Critico") {
                    Color(0xFFC62828) // rojo
                } else {
                    Color(0xFFFFA500) // naranjo
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
}