package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Pantalla de bienvenida: fondo rojo de marca, logo y lo que hace la app
@Composable
fun HomeScreen(
    onContinuar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AriztiaRed, AriztiaDarkRed)))
    ) {
        // Círculos concéntricos de fondo (solo decoración)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centro = Offset(size.width * 0.5f, size.height * 0.30f)
            listOf(90f, 140f, 190f, 240f).forEach { radio ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.10f),
                    radius = radio.dp.toPx(),
                    center = centro,
                    style = Stroke(1.dp.toPx())
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.6f))

            // Logo grande: gota roja sobre cuadro blanco
            AriztiaLogo(tamano = 96, fondo = Color.White, colorGota = AriztiaRed)

            Spacer(Modifier.height(22.dp))

            Text(
                "ARIZTÍA",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                "WATER CONTROL",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "Control sanitario de bebederos para operaciones avícolas",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(Modifier.weight(0.5f))

            // Lo que permite hacer la app
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Caracteristica(Icons.Default.Thermostat, "Temperatura del agua por línea")
                Caracteristica(Icons.Default.NotificationsNone, "Alertas de advertencia y críticas")
                Caracteristica(Icons.Default.WaterDrop, "Registro de flushing con evidencia")
            }

            Spacer(Modifier.weight(0.5f))

            // Botón blanco con texto rojo, destaca sobre el fondo
            Button(
                onClick = onContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = AriztiaRed
                )
            ) {
                Text("Comenzar  ›", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(14.dp))

            Text(
                "MiApp_GrupoX · Prototipo académico DSY1105",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}

// Fila translúcida con ícono y texto
@Composable
private fun Caracteristica(icono: ImageVector, texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(texto, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}