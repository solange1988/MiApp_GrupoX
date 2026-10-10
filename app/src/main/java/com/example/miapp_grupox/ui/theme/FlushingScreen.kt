package com.example.miapp_grupox.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.miapp_grupox.model.FlusingEntity
import com.example.miapp_grupox.model.OpcionesBebedero
import com.example.miapp_grupox.model.vibrarConfirmacion
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FlusingScreen(
    registros: List<FlusingEntity>,
    onGuardar: (FlusingEntity) -> Unit,
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

    // Un estado de error por campo, para marcarlo en rojo
    var errorMotivo by remember { mutableStateOf(false) }
    var errorResponsable by remember { mutableStateOf(false) }
    var errorObservaciones by remember { mutableStateOf(false) }
    var errorFoto by remember { mutableStateOf(false) }

    // Recurso nativo: abre la aplicación de cámara y devuelve la foto
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            foto = bitmap
            errorFoto = false
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
        ) {
            AriztiaHeader(
                "Registro de flushing",
                "Control de limpieza de líneas de bebederos"
            )

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AriztiaSectionTitle(
                        "Nuevo registro",
                        "Completa los datos de la actividad realizada."
                    )
                }

                item {
                    AriztiaWhiteCard {
                        AriztiaDropdown(
                            label = "Granja",
                            opciones = OpcionesBebedero.granjas,
                            seleccion = granja,
                            onSeleccion = { granja = it }
                        )

                        Spacer(Modifier.height(8.dp))

                        AriztiaDropdown(
                            label = "Galpón",
                            opciones = OpcionesBebedero.galpones,
                            seleccion = galpon,
                            onSeleccion = { galpon = it }
                        )

                        Spacer(Modifier.height(8.dp))

                        AriztiaDropdown(
                            label = "Línea de bebederos",
                            opciones = OpcionesBebedero.lineas,
                            seleccion = linea,
                            onSeleccion = { linea = it }
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = responsable,
                            onValueChange = {
                                responsable = it
                                errorResponsable = false
                            },
                            label = { Text("Responsable") },
                            isError = errorResponsable,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(Modifier.height(8.dp))

                        AriztiaDropdown(
                            label = "Motivo del flushing",
                            opciones = OpcionesBebedero.motivosFlushing,
                            seleccion = motivo,
                            onSeleccion = {
                                motivo = it
                                errorMotivo = false
                            },
                            isError = errorMotivo
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = observaciones,
                            onValueChange = {
                                observaciones = it
                                errorObservaciones = false
                            },
                            label = { Text("Observaciones") },
                            isError = errorObservaciones,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        Spacer(Modifier.height(12.dp))

                        // Botón que abre la cámara (evidencia del flushing)
                        AriztiaSecondaryButton(
                            texto = "📷 Tomar foto de evidencia",
                            onClick = { tomarFoto() }
                        )

                        // La foto aparece con animación debajo del botón
                        AnimatedVisibility(visible = foto != null) {
                            foto?.let { bitmap ->
                                Column {
                                    Spacer(Modifier.height(8.dp))
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Foto de evidencia del flushing",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }
                            }
                        }

                        if (errorFoto) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Falta la foto de evidencia",
                                color = AriztiaRed
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Fecha y hora: ${fechaActual()}",
                            color = TextSecondary
                        )

                        Spacer(Modifier.height(12.dp))

                        AriztiaPrimaryButton(
                            texto = "Guardar registro",
                            onClick = {
                                errorMotivo = motivo.isBlank()
                                errorResponsable = responsable.isBlank()
                                errorObservaciones = observaciones.isBlank()
                                errorFoto = foto == null

                                if (errorMotivo || errorResponsable ||
                                    errorObservaciones || errorFoto
                                ) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Todos los campos son obligatorios"
                                        )
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
                                        snackbarHostState.showSnackbar(
                                            "Flushing registrado correctamente"
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                item {
                    AriztiaSectionTitle(
                        "Historial de flushing",
                        "${registros.size} registros guardados"
                    )
                }

                if (registros.isEmpty()) {
                    item {
                        AriztiaWhiteCard {
                            Text("Sin registros", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Los registros que guardes aparecerán aquí.",
                                color = TextSecondary
                            )
                        }
                    }
                }

                items(registros, key = { it.id }) { registro ->
                    AriztiaWhiteCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${registro.granja} · ${registro.galpon}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(registro.linea, color = AriztiaRed)
                            }

                            EstadoBadge("Registrado")
                        }

                        HorizontalDivider(Modifier.padding(vertical = 10.dp))

                        TextoRegistro("Responsable", registro.responsable)
                        TextoRegistro("Motivo", registro.motivo)
                        TextoRegistro("Fecha", registro.fecha)

                        if (registro.observaciones.isNotBlank()) {
                            TextoRegistro("Observaciones", registro.observaciones)
                        }
                    }
                }

                item {
                    AriztiaSecondaryButton("Volver", onVolver)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TextoRegistro(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text("$etiqueta: ", color = TextPrimary)
        Text(valor, color = TextSecondary)
    }
}

private fun fechaActual(): String {
    return SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    ).format(Date())
}