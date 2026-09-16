package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.miapp_grupox.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar

// Pantalla principal (Home) de la app, construida con Jetpack Compose
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // Scaffold da la estructura base: una barra superior + el contenido
    Scaffold(
        topBar = {
            // Barra superior con el título de la app
            TopAppBar(title = { Text("Mi App Kotlin") })
        }
    ) { innerPadding ->
        // Column organiza los elementos uno debajo del otro
        Column(
            modifier = Modifier
                .padding(innerPadding)   // respeta el espacio de la barra superior
                .fillMaxSize()            // ocupa toda la pantalla disponible
                .padding(16.dp),          // margen extra alrededor de todo
            verticalArrangement = Arrangement.spacedBy(20.dp) // espacio uniforme entre elementos
        ) {
            // Texto de bienvenida
            Text(
                text = "¡Bienvenido!",
                color = MaterialTheme.colorScheme.primary
            )

            // Botón simple (todavía sin acción real)
            Button(onClick = { /* acción futura */ }) {
                Text("Presióname")
            }
            // Fila con dos textos lado a lado, usando Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Grupo X")
                Text(
                    text = "MVVM + Compose",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Imagen del logo de la app
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de la app",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}