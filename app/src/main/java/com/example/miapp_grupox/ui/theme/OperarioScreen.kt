package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miapp_grupox.model.LineBebedero

@Composable
fun OperarioScreen(
    onTemperaturas: () -> Unit,
    onFlushing: () -> Unit,
    onAlertas: () -> Unit,
    onInicio: () -> Unit,
    lineas: List<LineBebedero> = emptyList()
) {
    val criticas = lineas.count { it.estado == "Crítico" }
    val advertencias = lineas.count { it.estado == "Advertencia" }
    val normales = lineas.size - criticas - advertencias

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        AriztiaHeader(
            titulo = "Hola, Operario",
            subtitulo = "Panel del operario"
        )

        LazyColumn(
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.navigationBarsPadding()
        ) {
            // ---------- Estado general de la operación ----------
            item {
                ResumenOperacion(lineas, criticas, advertencias)
            }

            // ---------- Dona con las líneas por estado ----------
            item {
                AriztiaSectionTitle("Estado de las líneas", "Resumen de la última medición de cada línea")
            }
            item {
                AriztiaDonutEstados(normales, advertencias, criticas)
            }

            // ---------- Accesos rápidos ----------
            item {
                AriztiaSectionTitle("¿Qué necesitas hacer?")
            }
            item {
                AriztiaActionCard(
                    titulo = "Temperaturas",
                    descripcion = "Registrar y consultar mediciones",
                    onClick = onTemperaturas,
                    icono = Icons.Default.Thermostat
                )
            }
            item {
                AriztiaActionCard(
                    titulo = "Registrar flushing",
                    descripcion = "Lavado de líneas con foto de evidencia",
                    onClick = onFlushing,
                    icono = Icons.Default.WaterDrop
                )
            }
            item {
                AriztiaActionCard(
                    titulo = "Revisar alertas",
                    descripcion = if (criticas + advertencias > 0) {
                        "${criticas + advertencias} líneas requieren revisión"
                    } else {
                        "Sin alertas activas"
                    },
                    onClick = onAlertas,
                    icono = Icons.Default.NotificationsNone,
                    colorIcono = EstadoAdvertencia,
                    fondoIcono = EstadoAdvertenciaBackground
                )
            }

            item {
                Column {
                    Spacer(Modifier.height(4.dp))
                    AriztiaSecondaryButton(
                        texto = "Cerrar sesión",
                        onClick = onInicio
                    )
                }
            }
        }
    }
}

// Tarjeta oscura: mensaje según la peor línea + máxima, promedio y total
@Composable
private fun ResumenOperacion(
    lineas: List<LineBebedero>,
    criticas: Int,
    advertencias: Int
) {
    val (titulo, detalle, color) = when {
        criticas > 0 -> Triple(
            "Atención requerida",
            "$criticas ${if (criticas == 1) "línea crítica necesita" else "líneas críticas necesitan"} revisión",
            AriztiaRed
        )
        advertencias > 0 -> Triple(
            "Revisión sugerida",
            "$advertencias ${if (advertencias == 1) "línea" else "líneas"} en advertencia",
            EstadoAdvertencia
        )
        else -> Triple("Operación estable", "Todas las líneas en rango normal", EstadoNormal)
    }

    val maxima = lineas.maxOfOrNull { it.temperatura }
    val promedio = if (lineas.isEmpty()) null else lineas.map { it.temperatura }.average()

    AriztiaHeroCard(
        etiqueta = "Estado de la operación",
        titulo = titulo,
        detalle = detalle,
        icono = if (criticas + advertencias > 0) Icons.Default.Thermostat else Icons.Default.CheckCircle,
        colorIcono = color
    ) {
        Spacer(Modifier.height(18.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
        Spacer(Modifier.height(14.dp))

        Row(modifier = Modifier.height(44.dp)) {
            DatoResumen(maxima?.let { "%.1f°".format(it) } ?: "--", "Máxima", Modifier.weight(1f))
            SeparadorVertical()
            DatoResumen(promedio?.let { "%.1f°".format(it) } ?: "--", "Promedio", Modifier.weight(1f))
            SeparadorVertical()
            DatoResumen("${lineas.size}", "Líneas", Modifier.weight(1f))
        }
    }
}

@Composable
private fun DatoResumen(valor: String, etiqueta: String, modifier: Modifier) {
    Column(modifier = modifier.padding(start = 10.dp)) {
        Text(valor, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(etiqueta, color = DarkCardText, fontSize = 10.sp)
    }
}

@Composable
private fun SeparadorVertical() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(Color.White.copy(alpha = 0.12f))
    )
}