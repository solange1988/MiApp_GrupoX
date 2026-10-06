
package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onContinuar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        AriztiaHeader(
            titulo = "Control sanitario",
            subtitulo = "Monitoreo de bebederos y flushing"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = AriztiaLightRed
            ) {
                Text(
                    "AB",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = AriztiaRed,
                    modifier = Modifier.padding(28.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Bienvenido",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Sistema de monitoreo y registro operativo",
                color = TextSecondary
            )

            Spacer(Modifier.height(30.dp))

            AriztiaPrimaryButton(
                texto = "Ingresar a la aplicación",
                onClick = onContinuar
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "MiApp_GrupoX · Prototipo académico",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}