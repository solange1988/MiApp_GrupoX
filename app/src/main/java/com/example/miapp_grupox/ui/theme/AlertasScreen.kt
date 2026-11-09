package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miapp_grupox.model.RangosTemperatura
import com.example.miapp_grupox.viewmodel.AlertasUiState

@Composable
fun AlertasScreen(
    estado: AlertasUiState,
    onReintentar: () -> Unit,
    onVolver: () -> Unit
) {
    val alertas = estado.alertas
    val criticas = alertas.count { it.nivel == "Crítico" }
    val advertencias = alertas.count { it.nivel == "Advertencia" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        AriztiaHeader(
            "Centro de alertas",
            "Seguimiento de las condiciones registradas"
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AriztiaMetricCard(
                        "Críticas", criticas.toString(), "Estado crítico",
                        EstadoCritico, Modifier.weight(1f)
                    )
                    AriztiaMetricCard(
                        "Advertencias", advertencias.toString(), "Requieren revisión",
                        EstadoAdvertenciaTexto, Modifier.weight(1f)
                    )
                }
            }

            item {
                AriztiaSectionTitle(
                    "Alertas activas",
                    if (estado.desdeServidor) "Datos en línea desde el servidor de monitoreo."
                    else "Las líneas normales no aparecen en esta lista."
                )
            }

            // Cargando
            if (estado.cargando) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EstadoCritico)
                    }
                }
            }

            // Sin conexión: aviso + reintentar
            estado.mensajeError?.let { mensaje ->
                item {
                    AriztiaWhiteCard {
                        Text(mensaje, color = EstadoAdvertenciaTexto)
                        Spacer(Modifier.height(10.dp))
                        AriztiaSecondaryButton("Reintentar", onReintentar)
                    }
                }
            }

            if (!estado.cargando && alertas.isEmpty()) {
                item {
                    AriztiaWhiteCard {
                        Text("Sin alertas activas", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Todas las líneas están en estado normal según los límites configurados.",
                            color = TextSecondary
                        )
                    }
                }
            }

            items(alertas) { alerta ->
                AriztiaWhiteCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(alerta.granja, style = MaterialTheme.typography.titleMedium)
                            Text("${alerta.galpon} · ${alerta.linea}", color = TextSecondary)
                        }
                        EstadoBadge(alerta.nivel)
                    }

                    Spacer(Modifier.height(10.dp))

                    Text("Rango de temperatura", color = TextSecondary)

                    // Se muestra el rango, no el número exacto
                    Text(
                        RangosTemperatura.etiquetaDe(alerta.temperatura),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (alerta.nivel == "Crítico") EstadoCritico else EstadoAdvertenciaTexto
                    )

                    Text("Fecha: ${alerta.fecha}", color = TextSecondary)
                }
            }

            item {
                AriztiaSecondaryButton("Volver", onVolver)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}