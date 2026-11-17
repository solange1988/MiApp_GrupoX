package com.example.miapp_grupox.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miapp_grupox.model.FlusingEntity
import com.example.miapp_grupox.model.LineBebedero
import com.example.miapp_grupox.viewmodel.ClimaUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupervisorScreen(
    lineas: List<LineBebedero>,
    registros: List<FlusingEntity>,
    onTemperaturas: () -> Unit,
    onAlertas: () -> Unit,
    onFlushing: () -> Unit,
    onInicio: () -> Unit,
    clima: ClimaUiState = ClimaUiState(),
    onReintentarClima: () -> Unit = {},
    onSincronizar: () -> Unit = {}
) {
    val criticas = lineas.count { it.estado == "Crítico" }
    val advertencias = lineas.count { it.estado == "Advertencia" }
    val normales = lineas.count { it.estado == "Normal" }

    // Operatividad: porcentaje de líneas en estado normal
    val operatividad = if (lineas.isEmpty()) 0 else normales * 100 / lineas.size

    // Agrupa las líneas por granja para "Estado de instalaciones"
    val granjas = lineas.groupBy { it.granja }.toSortedMap()
    val galpones = lineas.map { it.granja to it.galpon }.distinct().size

    // Fecha de hoy en español, ej: "LUNES, 16 NOV"
    val hoy = SimpleDateFormat("EEEE, d MMM", Locale("es", "CL")).format(Date()).uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        AriztiaHeader(
            titulo = "Panel regional",
            subtitulo = "Supervisor"
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.navigationBarsPadding()
        ) {
            // ---------- Visión general + sincronizar ----------
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            hoy,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                        Text("Visión general", fontSize = 24.sp, color = TextPrimary)
                        Text(
                            "${granjas.size} granjas · $galpones galpones · ${lineas.size} líneas",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    OutlinedButton(
                        onClick = onSincronizar,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CardBackground,
                            contentColor = TextPrimary
                        )
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Sincronizar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ---------- Operatividad y alertas activas ----------
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TarjetaIndicador(
                        icono = Icons.Default.Check,
                        colorIcono = EstadoNormal,
                        fondoIcono = EstadoNormalBackground,
                        etiqueta = "Operatividad",
                        valor = "$operatividad%",
                        detalle = "$normales de ${lineas.size} líneas normales",
                        colorDetalle = EstadoNormal,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaIndicador(
                        icono = Icons.Default.NotificationsNone,
                        colorIcono = AriztiaRed,
                        fondoIcono = AriztiaLightRed,
                        etiqueta = "Alertas activas",
                        valor = "${criticas + advertencias}",
                        detalle = "$criticas ${if (criticas == 1) "crítica" else "críticas"}",
                        colorDetalle = AriztiaRed,
                        modifier = Modifier.weight(1f),
                        onClick = onAlertas
                    )
                }
            }

            // ---------- Clima (API externa Open-Meteo) ----------
            item {
                TarjetaClima(clima, onReintentarClima)
            }

            // ---------- Estado de instalaciones por granja ----------
            item {
                AriztiaSectionTitle("Estado de instalaciones", "Desempeño por granja · toca para ver sus galpones")
            }

            if (granjas.isEmpty()) {
                item {
                    AriztiaWhiteCard {
                        Text("Sin líneas registradas", fontWeight = FontWeight.Medium)
                        Text(
                            "Sincroniza con el servidor para ver las instalaciones.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            items(granjas.entries.toList(), key = { it.key }) { (granja, lineasGranja) ->
                TarjetaGranja(granja, lineasGranja)
            }

            // ---------- Accesos rápidos ----------
            item {
                AriztiaSectionTitle("Accesos rápidos")
            }
            item {
                AriztiaActionCard(
                    titulo = "Temperaturas",
                    descripcion = "${lineas.size} líneas registradas",
                    onClick = onTemperaturas,
                    icono = Icons.Default.Thermostat
                )
            }
            item {
                AriztiaActionCard(
                    titulo = "Centro de alertas",
                    descripcion = "${criticas + advertencias} líneas requieren revisión",
                    onClick = onAlertas,
                    icono = Icons.Default.NotificationsNone,
                    colorIcono = EstadoAdvertencia,
                    fondoIcono = EstadoAdvertenciaBackground
                )
            }
            item {
                AriztiaActionCard(
                    titulo = "Seguimiento de flushing",
                    descripcion = "${registros.size} registros guardados",
                    onClick = onFlushing,
                    icono = Icons.Default.WaterDrop
                )
            }

            item {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    AriztiaSecondaryButton("Cerrar sesión", onInicio)
                }
            }
        }
    }
}

// Tarjeta chica con ícono, número grande y detalle
@Composable
private fun TarjetaIndicador(
    icono: ImageVector,
    colorIcono: Color,
    fondoIcono: Color,
    etiqueta: String,
    valor: String,
    detalle: String,
    colorDetalle: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground,
            disabledContainerColor = CardBackground
        ),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(fondoIcono, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        etiqueta.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = TextSecondary
                    )
                    Text(valor, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(detalle, fontSize = 11.sp, color = colorDetalle)
        }
    }
}

// Clima de Melipilla en tarjeta oscura (API externa)
@Composable
private fun TarjetaClima(clima: ClimaUiState, onReintentar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "CLIMA EN MELIPILLA · OPEN-METEO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = DarkCardText
                )
                Spacer(Modifier.height(8.dp))

                when {
                    clima.cargando -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    clima.error != null -> {
                        Text(clima.error, color = Color.White, fontSize = 14.sp)
                        TextButton(onClick = onReintentar, contentPadding = PaddingValues(0.dp)) {
                            Text("Reintentar", color = AriztiaButtonRed, fontWeight = FontWeight.Bold)
                        }
                    }

                    clima.temperatura != null -> {
                        Text(
                            "${"%.1f".format(clima.temperatura)}°C",
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Humedad ${clima.humedad ?: "-"} % · temperatura ambiente",
                            color = DarkCardText,
                            fontSize = 12.sp
                        )
                    }

                    else -> {
                        Text("Sin datos de clima", color = DarkCardText, fontSize = 13.sp)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFC107))
            }
        }
    }
}

// Granja con anillo de % de líneas normales; al tocarla muestra sus galpones
@Composable
private fun TarjetaGranja(granja: String, lineasGranja: List<LineBebedero>) {
    var abierta by remember { mutableStateOf(false) }

    val normales = lineasGranja.count { it.estado == "Normal" }
    val advertencias = lineasGranja.count { it.estado == "Advertencia" }
    val criticas = lineasGranja.count { it.estado == "Crítico" }
    val porcentaje = normales * 100 / lineasGranja.size

    // El peor estado de la granja define la píldora
    val peorEstado = when {
        criticas > 0 -> "Crítico"
        advertencias > 0 -> "Advertencia"
        else -> "Normal"
    }
    val galpones = lineasGranja.groupBy { it.galpon }.toSortedMap()

    Card(
        onClick = { abierta = !abierta },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnilloPorcentaje(porcentaje, colorEstado(peorEstado))
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(granja, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Text(
                        "${galpones.size} galpones · ${lineasGranja.size} líneas",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                EstadoBadge(peorEstado)
                Icon(
                    if (abierta) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (abierta) "Ocultar galpones" else "Ver galpones",
                    tint = TextPrimary
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(Modifier.height(10.dp))

            // Conteo por estado
            Row {
                ConteoEstado(normales, "Normales", EstadoNormal, Modifier.weight(1f))
                ConteoEstado(advertencias, "Atención", EstadoAdvertencia, Modifier.weight(1f))
                ConteoEstado(criticas, "Críticas", EstadoCritico, Modifier.weight(1f))
            }

            // Galpones de la granja (se abren con animación)
            AnimatedVisibility(visible = abierta) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    galpones.forEach { (galpon, lineasGalpon) ->
                        val estadoGalpon = when {
                            lineasGalpon.any { it.estado == "Crítico" } -> "Crítico"
                            lineasGalpon.any { it.estado == "Advertencia" } -> "Advertencia"
                            else -> "Normal"
                        }
                        val promedio = lineasGalpon.map { it.temperatura }.average()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AppBackground, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                EstadoBadge(estadoGalpon)
                                Spacer(Modifier.height(4.dp))
                                Text(galpon, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    "${lineasGalpon.size} ${if (lineasGalpon.size == 1) "línea activa" else "líneas activas"}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "${"%.1f".format(promedio)}°",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("promedio", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Anillo que se llena con animación según el porcentaje
@Composable
private fun AnilloPorcentaje(porcentaje: Int, color: Color) {
    val progreso by animateFloatAsState(
        targetValue = porcentaje / 100f,
        animationSpec = tween(durationMillis = 900),
        label = "anilloGranja"
    )

    Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(52.dp)) {
            val grosor = 5.dp.toPx()
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(grosor)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progreso,
                useCenter = false,
                style = Stroke(grosor, cap = StrokeCap.Round)
            )
        }
        Text("$porcentaje%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun ConteoEstado(cantidad: Int, texto: String, color: Color, modifier: Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).background(color, RoundedCornerShape(50)))
        Spacer(Modifier.width(6.dp))
        Text("$cantidad", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.width(4.dp))
        Text(texto, fontSize = 10.sp, color = TextSecondary)
    }
}