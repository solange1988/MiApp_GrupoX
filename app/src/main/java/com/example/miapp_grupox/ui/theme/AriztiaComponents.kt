package com.example.miapp_grupox.ui.theme


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ============================================================
// HEADER (barra blanca del prototipo)
// Texto chico arriba + título grande. Si recibe onVolver, muestra flecha atrás.
// ============================================================

@Composable
fun AriztiaHeader(
    titulo: String,
    subtitulo: String,
    onVolver: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onVolver != null) {
                // Botón cuadrado con borde, como en el prototipo
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, BorderColor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subtitulo.uppercase(),
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = titulo,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            AriztiaLogo(tamano = 40)
        }

        HorizontalDivider(color = BorderColor)
    }
}


// ============================================================
// LOGO (gota blanca sobre cuadro rojo)
// ============================================================

@Composable
fun AriztiaLogo(
    tamano: Int = 44,
    fondo: Color = AriztiaRed,
    colorGota: Color = Color.White
) {
    Box(
        modifier = Modifier
            .size(tamano.dp)
            .background(fondo, RoundedCornerShape((tamano / 3.5).dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = null,
            tint = colorGota,
            modifier = Modifier.size((tamano / 2).dp)
        )
    }
}


// ============================================================
// TÍTULO DE SECCIÓN (texto chico en mayúsculas + título)
// ============================================================

@Composable
fun AriztiaSectionTitle(
    titulo: String,
    descripcion: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp)
    ) {
        Text(
            text = titulo,
            color = TextPrimary,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium
        )

        if (descripcion.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = descripcion,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}


// ============================================================
// ESTADO (píldora con punto de color)
// ============================================================

@Composable
fun EstadoBadge(
    estado: String
) {
    val texto = when (estado.trim().lowercase()) {
        "crítico", "critico" -> "Crítico"
        "advertencia" -> "Atención"
        else -> estado.trim().replaceFirstChar { it.uppercase() }
    }

    Surface(
        color = fondoEstado(estado),
        shape = RoundedCornerShape(50.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(colorEstado(estado), CircleShape)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = texto,
                color = colorEstado(estado),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ============================================================
// TARJETA DE MÉTRICA
// ============================================================

@Composable
fun AriztiaMetricCard(
    titulo: String,
    valor: String,
    descripcion: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = titulo.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = valor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = descripcion,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}


// ============================================================
// TARJETA OSCURA DE RESUMEN ("Estado de la operación")
// ============================================================

@Composable
fun AriztiaHeroCard(
    etiqueta: String,
    titulo: String,
    detalle: String,
    icono: ImageVector,
    colorIcono: Color = AriztiaRed,
    contenidoInferior: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = etiqueta.uppercase(),
                        color = DarkCardText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = titulo,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = detalle,
                        color = DarkCardText,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(colorIcono, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, contentDescription = null, tint = Color.White)
                }
            }
            contenidoInferior()
        }
    }
}


// ============================================================
// DONA DE ESTADOS (Normal / Atención / Crítico)
// ============================================================

@Composable
fun AriztiaDonutEstados(
    normales: Int,
    advertencias: Int,
    criticos: Int
) {
    val total = normales + advertencias + criticos

    AriztiaWhiteCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(110.dp)) {
                    val grosor = 14.dp.toPx()
                    val arco = Size(size.width - grosor, size.height - grosor)
                    val inicio = Offset(grosor / 2, grosor / 2)
                    var angulo = -90f

                    if (total == 0) {
                        drawArc(
                            color = BorderColor,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = inicio,
                            size = arco,
                            style = Stroke(grosor)
                        )
                    } else {
                        listOf(
                            normales to EstadoNormal,
                            advertencias to EstadoAdvertencia,
                            criticos to EstadoCritico
                        ).forEach { (cantidad, color) ->
                            if (cantidad > 0) {
                                val barrido = 360f * cantidad / total
                                drawArc(
                                    color = color,
                                    startAngle = angulo + 2f,
                                    sweepAngle = barrido - 4f,
                                    useCenter = false,
                                    topLeft = inicio,
                                    size = arco,
                                    style = Stroke(grosor, cap = StrokeCap.Butt)
                                )
                                angulo += barrido
                            }
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$total", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("líneas", fontSize = 10.sp, color = TextSecondary)
                }
            }

            Spacer(Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                FilaLeyenda("Normal", normales, EstadoNormal)
                HorizontalDivider(color = BorderColor)
                FilaLeyenda("Atención", advertencias, EstadoAdvertencia)
                HorizontalDivider(color = BorderColor)
                FilaLeyenda("Crítico", criticos, EstadoCritico)
            }
        }
    }
}

@Composable
private fun FilaLeyenda(texto: String, cantidad: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(10.dp))
        Text(texto, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.weight(1f))
        Text("$cantidad", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}


// ============================================================
// BOTÓN PRINCIPAL
// ============================================================

@Composable
fun AriztiaPrimaryButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), spotColor = AriztiaButtonRed),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AriztiaButtonRed,
            contentColor = Color.White
        )
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}


// ============================================================
// BOTÓN SECUNDARIO (blanco con borde)
// ============================================================

@Composable
fun AriztiaSecondaryButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardBackground,
            contentColor = TextPrimary
        )
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold)
    }
}


// ============================================================
// TARJETA BLANCA (borde suave, sin sombra fuerte)
// ============================================================

@Composable
fun AriztiaWhiteCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            content()
        }
    }
}


// ============================================================
// ACCESO RÁPIDO (ícono en cuadro de color + título + flecha)
// ============================================================

@Composable
fun AriztiaActionCard(
    titulo: String,
    descripcion: String,
    onClick: () -> Unit,
    icono: ImageVector = Icons.Default.WaterDrop,
    colorIcono: Color = AriztiaRed,
    fondoIcono: Color = AriztiaLightRed
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(fondoIcono),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = colorIcono)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = descripcion,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextPrimary
            )
        }
    }
}