package com.example.miapp_grupox.ui.theme



import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Pantalla del Operario: acceso a alertas, registro de flushing y temperaturas
@Composable
fun OperarioScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pantalla Operario",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Bienvenido Operario")

        Spacer(modifier = Modifier.height(20.dp))

        // Botón: ver alertas activas
        Button(
            onClick = { navController.navigate("alertas") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alertas Activas")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón: registrar flushing
        Button(
            onClick = { navController.navigate("flushing") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Flushing")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón: registrar/consultar temperaturas
        Button(
            onClick = { navController.navigate("temperaturas") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Temperaturas")
        }
    }
}