package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miapp_grupox.model.LoginValidator
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    // Recibe el perfil con el que se ingresó: "operario" o "supervisor"
    onLoginExitoso: (String) -> Unit,
    onVolver: () -> Unit
) {
    // Mensajes al pie de la pantalla (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var verClave by remember { mutableStateOf(false) }
    var errorUsuario by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = CardBackground,
        // El fondo rojo llega hasta arriba, detrás de la barra de estado
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ---------- Parte roja superior ----------
            EncabezadoLogin()

            // ---------- Hoja blanca con el formulario ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 330.dp)
                    .background(
                        CardBackground,
                        RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                // Pequeña barra gris al centro (como una hoja deslizable)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(38.dp)
                        .height(4.dp)
                        .background(BorderColor, RoundedCornerShape(50))
                )

                Spacer(Modifier.height(20.dp))

                Text("Bienvenido", fontSize = 26.sp, color = TextPrimary)
                Text(
                    "Ingresa con tu usuario de operario o supervisor",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(Modifier.height(22.dp))

                EtiquetaCampo("Usuario")
                // (el Box lleva el testTag para las pruebas de interfaz)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("campo_usuario")
                ) {
                    AriztiaTextField(
                        value = usuario,
                        onValueChange = {
                            usuario = it
                            errorUsuario = null
                        },
                        label = "operario o supervisor",
                        iconoInicial = Icons.Default.Person,
                        error = errorUsuario
                    )
                }

                Spacer(Modifier.height(10.dp))

                EtiquetaCampo("Contraseña")
                // Contraseña: candado y botón para ver u ocultar
                OutlinedTextField(
                    value = clave,
                    onValueChange = {
                        clave = it
                        errorClave = null
                    },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { verClave = !verClave }) {
                            Icon(
                                imageVector = if (verClave) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (verClave) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                }
                            )
                        }
                    },
                    isError = errorClave != null,
                    supportingText = errorClave?.let { mensaje -> { Text(mensaje) } },
                    visualTransformation = if (verClave) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AriztiaRed,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = AriztiaRed,
                        focusedLeadingIconColor = AriztiaRed,
                        cursorColor = AriztiaRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("campo_clave"),
                    singleLine = true
                )

                Spacer(Modifier.height(20.dp))

                AriztiaPrimaryButton(
                    texto = "Ingresar al panel  ›",
                    onClick = {
                        // La validación vive en LoginValidator, no en la pantalla
                        val resultado = LoginValidator.validar(usuario, clave)
                        errorUsuario = resultado.errorUsuario
                        errorClave = resultado.errorClave

                        val perfil = resultado.perfil
                        if (resultado.exitoso && perfil != null) {
                            onLoginExitoso(perfil)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    resultado.mensaje ?: "No se pudo iniciar sesión"
                                )
                            }
                        }
                    },
                    modifier = Modifier.testTag("boton_ingresar")
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    "Usuarios de prueba: operario o supervisor · clave 1234",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                TextButton(
                    onClick = onVolver,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Volver al inicio", color = AriztiaRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Parte roja con logo, frase principal y círculos decorativos
@Composable
private fun EncabezadoLogin() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(370.dp)
            .background(Brush.verticalGradient(listOf(AriztiaDarkRed, AriztiaRed)))
    ) {
        // Círculos concéntricos y gota de fondo (solo decoración)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centro = Offset(size.width * 0.85f, size.height * 0.45f)
            listOf(70f, 110f, 150f).forEach { radio ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.18f),
                    radius = radio.dp.toPx(),
                    center = centro,
                    style = Stroke(1.dp.toPx())
                )
            }
        }
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.25f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 135.dp, end = 34.dp)
                .size(70.dp)
        )

        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AriztiaLogo(tamano = 40, fondo = Color.White, colorGota = AriztiaRed)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("ARIZTÍA", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "WATER CONTROL",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            Text(
                "OPERACIONES AVÍCOLAS",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Agua segura.\nDecisiones claras.",
                color = Color.White,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Monitorea cada línea y actúa antes de que una alerta se convierta en un problema.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun EtiquetaCampo(texto: String) {
    Text(
        texto,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}