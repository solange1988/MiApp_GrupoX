package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miapp_grupox.model.AlertaUi
import com.example.miapp_grupox.model.RangosTemperatura
import com.example.miapp_grupox.viewmodel.AlertasUiState

@Composable
fun AlertasScreen(
    estado: AlertasUiState,
    onReintentar: () -> Unit,
    onVolver: () -> Unit
) {
    // Primero las críticas, después las advertencias
    val alertas = estado.alertas.sortedBy { if (it.nivel == "Crítico") 0 else 1 }
    val criticas = alertas.count { it.nivel == "Crítico" }
    val advertencias = alertas.count { it.nivel == "Advertencia" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        AriztiaHeader(
            titulo = "Centro de alertas",
            subtitulo = if (estado.desdeServidor) "En línea · ms-monitoreo" else "Datos del dispositivo",
            onVolver = onVolver
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.navigationBarsPadding()
        ) {
            // ---------- Resumen oscuro ----------
            item {
                AriztiaHeroCard(
                    etiqueta = "Requieren atención",
                    titulo = when (alertas.size) {
                        0 -> "Sin alertas activas"
                        1 -> "1 alerta activa"
                        else -> "${alertas.size} alertas activas"
                    },
                    detalle = "$criticas ${if (criticas == 1) "crítica" else "críticas"} · " +
                            "$advertencias ${if (advertencias == 1) "advertencia" else "advertencias"}",
                    icono = if (alertas.isEmpty()) Icons.Default.CheckCircle else Icons.Default.NotificationsNone,
                    colorIcono = if (alertas.isEmpty()) EstadoNormal else AriztiaRed
                )
            }

            // ---------- Cargando ----------
            if (estado.cargando) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AriztiaRed)
                    }
                }
            }

            // ---------- Sin conexión: aviso + reintentar ----------
            estado.mensajeError?.let { mensaje ->
                item {
                    Surface(
                        color = EstadoAdvertenciaBackground,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = EstadoAdvertenciaTexto
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(mensaje, color = EstadoAdvertenciaTexto, fontSize = 13.sp)
                            }
                            Spacer(Modifier.height(10.dp))
                            AriztiaSecondaryButton("Reintentar", onReintentar)
                        }
                    }
                }
            }

            // ---------- Sin alertas ----------
            if (!estado.cargando && alertas.isEmpty()) {
                item {
                    AriztiaWhiteCard {
                        Text("Todo en orden", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text(
                            "Todas las líneas están en estado normal según los límites configurados.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (alertas.isNotEmpty()) {
                item {
                    AriztiaSectionTitle("Alertas activas", "Ordenadas por gravedad")
                }
            }

            items(alertas) { alerta ->
                TarjetaAlerta(alerta)
            }
        }
    }
}

// Tarjeta con borde de color a la izquierda y temperatura destacada
@Composable
private fun TarjetaAlerta(alerta: AlertaUi) {
    val color = colorEstado(alerta.nivel)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(color)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EstadoBadge(alerta.nivel)
                    Spacer(Modifier.weight(1f))
                    Text(alerta.fecha, fontSize = 11.sp, color = TextSecondary)
                }

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            alerta.granja.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                        Text(
                            "${alerta.linea} · ${alerta.galpon}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            "Temperatura sobre el umbral configurado",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    // Se muestra el rango, no el número exacto
                    Text(
                        RangosTemperatura.etiquetaDe(alerta.temperatura),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (alerta.nivel == "Crítico") EstadoCritico else EstadoAdvertenciaTexto
                    )
                }
            }
        }
    }
}