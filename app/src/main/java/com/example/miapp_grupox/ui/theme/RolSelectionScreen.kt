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

// Pantalla inicial: el usuario elige si entra como Operario o Supervisor
@Composable
fun RolSelectionScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla
        Text(
            text = "Seleccione su Rol",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Botón para entrar como Operario
        Button(
            onClick = {
                navController.navigate("operario")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Operario")
        }

        // Botón para entrar como Supervisor
        Button(
            onClick = {
                navController.navigate("supervisor")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Supervisor")
        }
    }
}