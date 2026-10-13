package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.miapp_grupox.model.LoginValidator
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
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
        containerColor = AppBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AriztiaHeader(
                titulo = "Iniciar sesión",
                subtitulo = "Ingresa con tu usuario y contraseña"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AriztiaWhiteCard {
                    // Usuario: ícono de persona y advertencia si hay error
                    AriztiaTextField(
                        value = usuario,
                        onValueChange = {
                            usuario = it
                            errorUsuario = null
                        },
                        label = "Usuario",
                        iconoInicial = Icons.Default.Person,
                        error = errorUsuario
                    )

                    Spacer(Modifier.height(8.dp))

                    // Contraseña: candado y botón para ver u ocultar
                    OutlinedTextField(
                        value = clave,
                        onValueChange = {
                            clave = it
                            errorClave = null
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
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
                        supportingText = errorClave?.let { mensaje ->
                            { Text(mensaje) }
                        },
                        visualTransformation = if (verClave) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    AriztiaPrimaryButton(
                        texto = "Ingresar",
                        onClick = {
                            // La validación vive en LoginValidator, no en la pantalla
                            val resultado = LoginValidator.validar(usuario, clave)
                            errorUsuario = resultado.errorUsuario
                            errorClave = resultado.errorClave

                            if (resultado.exitoso) {
                                onLoginExitoso()
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        resultado.mensaje ?: "No se pudo iniciar sesión"
                                    )
                                }
                            }
                        }
                    )
                }

                Text(
                    "Usuarios de prueba: operario o supervisor, contraseña 1234",
                    color = TextSecondary
                )

                AriztiaSecondaryButton("Volver", onVolver)
            }
        }
    }
}