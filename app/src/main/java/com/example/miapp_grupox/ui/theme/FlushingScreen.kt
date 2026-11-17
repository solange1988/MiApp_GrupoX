package com.example.miapp_grupox.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.miapp_grupox.model.FlusingEntity
import com.example.miapp_grupox.model.OpcionesBebedero
import com.example.miapp_grupox.model.ValidadorFlushing
import com.example.miapp_grupox.model.vibrarConfirmacion
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FlusingScreen(
    registros: List<FlusingEntity>,
    onGuardar: (FlusingEntity) -> Unit,
    onEliminar: (Int) -> Unit,
    onActualizarObservacion: (FlusingEntity, String) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    // Estado y corrutina para mostrar mensajes al pie de la pantalla (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var granja by remember { mutableStateOf(OpcionesBebedero.granjas[0]) }
    var galpon by remember { mutableStateOf(OpcionesBebedero.galpones[0]) }
    var linea by remember { mutableStateOf(OpcionesBebedero.lineas[0]) }
    var motivo by remember { mutableStateOf("") }
    var responsable by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    // Foto de evidencia tomada con la cámara (se guarda como Bitmap)
    var foto by remember { mutableStateOf<Bitmap?>(null) }

    // Errores por campo que entrega el validador (lógica fuera de la pantalla)
    var errores by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Estado de los diálogos de eliminar y editar del historial
    var registroAEliminar by remember { mutableStateOf<FlusingEntity?>(null) }
    var registroEditando by remember { mutableStateOf<FlusingEntity?>(null) }
    var textoEdicion by remember { mutableStateOf("") }
    var errorEdicion by remember { mutableStateOf<String?>(null) }

    // Paso actual del indicador: 1 Ubicación, 2 Detalles, 3 Evidencia lista
    val pasoActual = when {
        foto != null && motivo.isNotBlank() && responsable.isNotBlank() -> 3
        motivo.isNotBlank() || responsable.isNotBlank() -> 2
        else -> 1
    }

    // Recurso nativo: abre la aplicación de cámara y devuelve la foto
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            foto = bitmap
            errores = errores - "foto"
        }
    }

    // Pide el permiso de cámara al usuario; si lo acepta, abre la cámara
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            camaraLauncher.launch(null)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Sin permiso de cámara no se puede adjuntar evidencia"
                )
            }
        }
    }

    // Revisa si el permiso ya fue entregado antes de abrir la cámara
    fun tomarFoto() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            camaraLauncher.launch(null)
        } else {
            permisoLauncher.launch(Manifest.permission.CAMERA)
        }
    }

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
                titulo = "Registrar flushing",
                subtitulo = "Nuevo registro",
                onVolver = onVolver
            )

            LazyColumn(
                modifier = Modifier.testTag("lista_flushing"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ---------- Indicador de pasos ----------
                item {
                    IndicadorPasos(pasoActual)
                }

                // ---------- Ubicación seleccionada (tarjeta oscura) ----------
                item {
                    UbicacionSeleccionada(granja, galpon, linea)
                }

                // ---------- Ubicación ----------
                item { EtiquetaSeccion("Ubicación") }
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
                    }
                }

                // ---------- Detalles del procedimiento ----------
                item { EtiquetaSeccion("Detalles del procedimiento") }
                item {
                    AriztiaWhiteCard {
                        // Responsable: ícono de persona y error por campo
                        AriztiaTextField(
                            value = responsable,
                            onValueChange = {
                                responsable = it
                                errores = errores - "responsable"
                            },
                            label = "Responsable",
                            iconoInicial = Icons.Default.Person,
                            error = errores["responsable"]
                        )

                        Spacer(Modifier.height(10.dp))

                        AriztiaDropdown(
                            label = "Motivo del flushing",
                            opciones = OpcionesBebedero.motivosFlushing,
                            seleccion = motivo,
                            onSeleccion = {
                                motivo = it
                                errores = errores - "motivo"
                            },
                            isError = errores.containsKey("motivo")
                        )

                        errores["motivo"]?.let { mensaje ->
                            Spacer(Modifier.height(4.dp))
                            Text(mensaje, color = AriztiaRed, fontSize = 12.sp)
                        }

                        Spacer(Modifier.height(10.dp))

                        // Observaciones: ícono de lápiz y error por campo
                        AriztiaTextField(
                            value = observaciones,
                            onValueChange = {
                                observaciones = it
                                errores = errores - "observaciones"
                            },
                            label = "Describe brevemente el procedimiento",
                            iconoInicial = Icons.Default.Edit,
                            error = errores["observaciones"],
                            singleLine = false,
                            minLines = 3
                        )
                    }
                }

                // ---------- Evidencia ----------
                item { EtiquetaSeccion("Evidencia") }
                item {
                    Column {
                        // Recuadro con ícono de cámara (evidencia del flushing)
                        AriztiaCameraButton(onClick = { tomarFoto() })

                        // La foto aparece con animación debajo del recuadro
                        AnimatedVisibility(visible = foto != null) {
                            foto?.let { bitmap ->
                                Column {
                                    Spacer(Modifier.height(10.dp))
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Foto de evidencia del flushing",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                    )
                                }
                            }
                        }

                        errores["foto"]?.let { mensaje ->
                            Spacer(Modifier.height(4.dp))
                            Text(mensaje, color = AriztiaRed, fontSize = 12.sp)
                        }
                    }
                }

                // ---------- Fecha, hora y responsable ----------
                item {
                    DatosRegistro(responsable)
                }

                // ---------- Botón confirmar ----------
                item {
                    AriztiaPrimaryButton(
                        texto = "Confirmar registro  ✓",
                        onClick = {
                            // La validación se hace en ValidadorFlushing, no aquí
                            val resultado = ValidadorFlushing.validar(
                                responsable = responsable,
                                motivo = motivo,
                                observaciones = observaciones,
                                tieneFoto = foto != null
                            )
                            errores = resultado.errores

                            if (!resultado.valido) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Revisa los campos marcados")
                                }
                            } else {
                                onGuardar(
                                    FlusingEntity(
                                        granja = granja,
                                        galpon = galpon,
                                        linea = linea,
                                        responsable = responsable.trim(),
                                        motivo = motivo,
                                        observaciones = observaciones.trim(),
                                        fecha = fechaActual()
                                    )
                                )

                                // Vibración como confirmación táctil al guardar
                                vibrarConfirmacion(context)

                                responsable = ""
                                motivo = ""
                                observaciones = ""
                                foto = null
                                scope.launch {
                                    snackbarHostState.showSnackbar("Flushing registrado correctamente")
                                }
                            }
                        },
                        modifier = Modifier.testTag("boton_guardar_flushing")
                    )
                }

                // ---------- Historial (seguimiento) ----------
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        AriztiaSectionTitle(
                            "Seguimiento de flushing",
                            "${registros.size} registros guardados"
                        )
                    }
                }

                if (registros.isEmpty()) {
                    item {
                        AriztiaWhiteCard {
                            Text("Sin registros", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text(
                                "Los registros que guardes aparecerán aquí.",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                items(registros, key = { it.id }) { registro ->
                    TarjetaRegistro(
                        registro = registro,
                        esUltimo = registro == registros.last(),
                        onEditar = {
                            registroEditando = registro
                            textoEdicion = registro.observaciones
                            errorEdicion = null
                        },
                        onEliminar = { registroAEliminar = registro }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación antes de eliminar un registro
    registroAEliminar?.let { registro ->
        AlertDialog(
            onDismissRequest = { registroAEliminar = null },
            containerColor = CardBackground,
            title = { Text("Eliminar registro") },
            text = {
                Text(
                    "¿Seguro que quieres eliminar el flushing de ${registro.linea} " +
                            "(${registro.fecha})? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminar(registro.id)
                        registroAEliminar = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Registro eliminado")
                        }
                    },
                    modifier = Modifier.testTag("confirmar_eliminar")
                ) {
                    Text("Eliminar", color = AriztiaRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { registroAEliminar = null }) {
                    Text("Cancelar", color = TextPrimary)
                }
            }
        )
    }

    // Diálogo para corregir la observación de un registro
    registroEditando?.let { registro ->
        AlertDialog(
            onDismissRequest = { registroEditando = null },
            containerColor = CardBackground,
            title = { Text("Editar observación") },
            text = {
                AriztiaTextField(
                    value = textoEdicion,
                    onValueChange = {
                        textoEdicion = it
                        errorEdicion = null
                    },
                    label = "Observaciones",
                    iconoInicial = Icons.Default.Edit,
                    error = errorEdicion,
                    singleLine = false,
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    // La validación está en ValidadorFlushing, no en la pantalla
                    val error = ValidadorFlushing.validarObservacion(textoEdicion)
                    if (error != null) {
                        errorEdicion = error
                    } else {
                        onActualizarObservacion(registro, textoEdicion)
                        registroEditando = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Observación actualizada")
                        }
                    }
                }) {
                    Text("Guardar", color = AriztiaRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { registroEditando = null }) {
                    Text("Cancelar", color = TextPrimary)
                }
            }
        )
    }
}

// Tres círculos unidos por líneas: verde = listo, rojo = actual, gris = pendiente
@Composable
private fun IndicadorPasos(pasoActual: Int) {
    val nombres = listOf("Ubicación", "Detalles", "Evidencia")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        nombres.forEachIndexed { indice, nombre ->
            val numero = indice + 1
            val color = when {
                numero < pasoActual -> EstadoNormal
                numero == pasoActual -> AriztiaRed
                else -> BorderColor
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (numero < pasoActual) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text(
                            "$numero",
                            color = if (numero == pasoActual) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(nombre, fontSize = 10.sp, color = if (numero <= pasoActual) color else TextSecondary)
            }

            if (numero < nombres.size) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 14.dp, start = 6.dp, end = 6.dp)
                        .height(2.dp)
                        .background(if (numero < pasoActual) EstadoNormal else BorderColor)
                )
            }
        }
    }
}

// Tarjeta oscura con la línea elegida en el formulario
@Composable
private fun UbicacionSeleccionada(granja: String, galpon: String, linea: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AriztiaLogo(tamano = 46)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    "UBICACIÓN SELECCIONADA",
                    color = DarkCardText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    "$linea · $galpon",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(granja, color = DarkCardText, fontSize = 12.sp)
            }
        }
    }
}

// Fecha, hora y responsable del registro en tres columnas
@Composable
private fun DatosRegistro(responsable: String) {
    val ahora = Date()
    val fecha = SimpleDateFormat("dd MMM yyyy", Locale("es", "CL")).format(ahora)
    val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(ahora)

    AriztiaWhiteCard {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            DatoColumna("Fecha", fecha, Modifier.weight(1f))
            Box(Modifier.width(1.dp).fillMaxHeight().background(BorderColor))
            DatoColumna("Hora", hora, Modifier.weight(1f))
            Box(Modifier.width(1.dp).fillMaxHeight().background(BorderColor))
            DatoColumna("Responsable", responsable.ifBlank { "--" }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun DatoColumna(etiqueta: String, valor: String, modifier: Modifier) {
    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        Text(
            etiqueta.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextSecondary
        )
        Spacer(Modifier.height(4.dp))
        Text(valor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
    }
}

// Registro del historial con línea de tiempo a la izquierda
@Composable
private fun TarjetaRegistro(
    registro: FlusingEntity,
    esUltimo: Boolean,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // Línea de tiempo: ícono y raya vertical
        Column(
            modifier = Modifier
                .width(30.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EstadoNormal,
                modifier = Modifier.size(24.dp)
            )
            if (!esUltimo) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(BorderColor)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Estado animado: "En proceso" -> "Completado"
                    EstadoFlushingAnimado(registro.id)
                    Spacer(Modifier.weight(1f))
                    Text(registro.fecha, fontSize = 11.sp, color = TextSecondary)
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    "${registro.granja} · ${registro.galpon}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(registro.linea, color = AriztiaRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(8.dp))

                TextoRegistro("Responsable", registro.responsable)
                TextoRegistro("Motivo", registro.motivo)
                if (registro.observaciones.isNotBlank()) {
                    TextoRegistro("Observaciones", registro.observaciones)
                }

                Spacer(Modifier.height(12.dp))

                // Modificar y eliminar un registro del historial
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onEditar,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("boton_editar_${registro.id}"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Editar", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onEliminar,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("boton_eliminar_${registro.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AriztiaLightRed,
                            contentColor = AriztiaRed
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Eliminar", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EtiquetaSeccion(texto: String) {
    Text(
        texto.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = TextSecondary,
        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
    )
}

@Composable
private fun TextoRegistro(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text("$etiqueta: ", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(valor, color = TextSecondary, fontSize = 12.sp)
    }
}

private fun fechaActual(): String {
    return SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    ).format(Date())
}