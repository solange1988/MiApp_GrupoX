package com.example.miapp_grupox.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miapp_grupox.model.LineBebedero
import com.example.miapp_grupox.model.OpcionesBebedero
import com.example.miapp_grupox.model.RangosTemperatura
import com.example.miapp_grupox.model.ValidadorTemperatura
import com.example.miapp_grupox.model.vibrarConfirmacion
import kotlinx.coroutines.launch

@Composable
fun TemperaturasScreen(
    lineas: List<LineBebedero>,
    onGuardar: (String, String, String, Double) -> Unit,
    onVolver: () -> Unit,
    aviso: String? = null
) {
    val context = LocalContext.current

    // Estado y corrutina para mostrar mensajes al pie de la pantalla (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var granja by remember { mutableStateOf(OpcionesBebedero.granjas[0]) }
    var galpon by remember { mutableStateOf(OpcionesBebedero.galpones[0]) }
    var linea by remember { mutableStateOf(OpcionesBebedero.lineas[0]) }
    var rango by remember { mutableStateOf("") }

    // Errores por campo que entrega el validador (lógica fuera de la pantalla)
    var errores by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Filtro de la lista: "Todas", "Normal", "Advertencia" o "Crítico"
    var filtro by remember { mutableStateOf("Todas") }

    // Primero las críticas, luego advertencias y al final las normales
    val prioridad = mapOf("Crítico" to 0, "Advertencia" to 1, "Normal" to 2)
    val lineasFiltradas = lineas
        .filter { filtro == "Todas" || it.estado == filtro }
        .sortedBy { prioridad[it.estado] ?: 3 }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AppBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            AriztiaHeader(
                titulo = "Líneas de agua",
                subtitulo = "Control de temperaturas",
                onVolver = onVolver
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Aviso de conexión con el servidor (solo aparece si hay problema)
                aviso?.let { mensaje ->
                    item {
                        Surface(
                            color = EstadoAdvertenciaBackground,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                mensaje,
                                color = EstadoAdvertenciaTexto,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                item {
                    AriztiaSectionTitle("Registrar medición", "Elige la línea y el rango medido")
                }

                item {
                    AriztiaWhiteCard {
                        AriztiaDropdown(
                            label = "Granja",
                            opciones = OpcionesBebedero.granjas,
                            seleccion = granja,
                            onSeleccion = { granja = it }
                        )

                        Spacer(Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AriztiaDropdown(
                                label = "Galpón",
                                opciones = OpcionesBebedero.galpones,
                                seleccion = galpon,
                                onSeleccion = { galpon = it },
                                modifier = Modifier.weight(1f)
                            )
                            AriztiaDropdown(
                                label = "Línea",
                                opciones = OpcionesBebedero.lineas,
                                seleccion = linea,
                                onSeleccion = { linea = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        AriztiaDropdown(
                            label = "Rango de temperatura",
                            opciones = RangosTemperatura.etiquetas,
                            seleccion = rango,
                            onSeleccion = {
                                rango = it
                                errores = errores - "rango"
                            },
                            isError = errores.containsKey("rango")
                        )

                        errores["rango"]?.let { mensaje ->
                            Spacer(Modifier.height(4.dp))
                            Text(mensaje, color = AriztiaRed, fontSize = 12.sp)
                        }

                        Spacer(Modifier.height(14.dp))

                        AriztiaLightButton(
                            texto = "Guardar temperatura",
                            onClick = {
                                // La validación se hace en ValidadorTemperatura, no aquí
                                val resultado = ValidadorTemperatura.validar(rango)
                                errores = resultado.errores
                                val valor = RangosTemperatura.valorDe(rango)

                                if (!resultado.valido || valor == null) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Revisa los campos marcados")
                                    }
                                } else {
                                    onGuardar(granja, galpon, linea, valor)

                                    // Vibración como confirmación táctil al guardar
                                    vibrarConfirmacion(context)

                                    rango = ""
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Temperatura registrada correctamente")
                                    }
                                }
                            }
                        )
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Box(Modifier.weight(1f)) {
                            AriztiaSectionTitle("Líneas registradas", "Ordenadas por prioridad")
                        }
                        Text(
                            "${lineasFiltradas.size} resultados",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                // Filtros por estado
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item { ChipFiltro("Todas ${lineas.size}", filtro == "Todas") { filtro = "Todas" } }
                        item {
                            ChipFiltro("Normales ${lineas.count { it.estado == "Normal" }}", filtro == "Normal") {
                                filtro = "Normal"
                            }
                        }
                        item {
                            ChipFiltro("Atención ${lineas.count { it.estado == "Advertencia" }}", filtro == "Advertencia") {
                                filtro = "Advertencia"
                            }
                        }
                        item {
                            ChipFiltro("Críticas ${lineas.count { it.estado == "Crítico" }}", filtro == "Crítico") {
                                filtro = "Crítico"
                            }
                        }
                    }
                }

                if (lineasFiltradas.isEmpty()) {
                    item {
                        Text(
                            "No hay líneas con este estado.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                items(lineasFiltradas, key = { it.id }) { item ->
                    TarjetaLinea(item)
                }
            }
        }
    }
}

// Botón de filtro tipo píldora: negro si está activo, blanco si no
@Composable
private fun ChipFiltro(texto: String, activo: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (activo) TextPrimary else CardBackground,
        border = if (activo) null else BorderStroke(1.dp, BorderColor)
    ) {
        Text(
            texto,
            color = if (activo) Color.White else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun TarjetaLinea(linea: LineBebedero) {
    // Controla si el detalle de la tarjeta está visible
    var mostrarDetalle by remember { mutableStateOf(false) }

    // Animación: el color de la barra lateral cambia suavemente según el estado
    val colorEstadoAnimado by animateColorAsState(
        targetValue = colorEstado(linea.estado),
        animationSpec = tween(durationMillis = 600),
        label = "colorEstado"
    )

    // Animación: la barra de nivel se llena según la temperatura (15 °C a 35 °C)
    val nivel by animateFloatAsState(
        targetValue = ((linea.temperatura - 15.0) / 20.0).toFloat().coerceIn(0.05f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "nivel"
    )

    // Animación: la sombra de la tarjeta sube al abrir el detalle
    val elevacion by animateDpAsState(
        targetValue = if (mostrarDetalle) 8.dp else 0.dp,
        label = "elevacionCard"
    )

    Card(
        onClick = { mostrarDetalle = !mostrarDetalle },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(), // la tarjeta crece/encoge con animación
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevacion)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Barra de color a la izquierda según el estado
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(colorEstadoAnimado)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${linea.granja} · ${linea.galpon}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            linea.linea,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                    EstadoBadge(linea.estado)
                }

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        RangosTemperatura.etiquetaDe(linea.temperatura),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (mostrarDetalle) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = if (mostrarDetalle) "Ocultar detalle" else "Ver detalle",
                        tint = TextPrimary
                    )
                }

                Text("Actualizada: ${linea.fecha}", fontSize = 11.sp, color = TextSecondary)

                Spacer(Modifier.height(10.dp))

                // Barra de nivel de temperatura
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(BorderColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(nivel)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(colorEstadoAnimado)
                    )
                }

                // Animación: el detalle aparece y desaparece con efecto
                AnimatedVisibility(visible = mostrarDetalle) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        HorizontalDivider(color = BorderColor)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Valor registrado: ${"%.1f".format(linea.temperatura)} °C",
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Criterio: Normal menos de 27 °C · Advertencia de 27 a 29,9 °C · Crítico desde 30 °C",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}