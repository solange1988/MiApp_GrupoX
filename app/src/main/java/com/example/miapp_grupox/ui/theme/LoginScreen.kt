package com.example.miapp_grupox.ui.theme



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
                    OutlinedTextField(
                        value = usuario,
                        onValueChange = {
                            usuario = it
                            errorUsuario = null
                        },
                        label = { Text("Usuario") },
                        isError = errorUsuario != null,
                        trailingIcon = if (errorUsuario != null) {
                            { Text("⚠") }
                        } else null,
                        supportingText = errorUsuario?.let { mensaje ->
                            { Text(mensaje) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = clave,
                        onValueChange = {
                            clave = it
                            errorClave = null
                        },
                        label = { Text("Contraseña") },
                        isError = errorClave != null,
                        visualTransformation = if (verClave) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        trailingIcon = {
                            TextButton(onClick = { verClave = !verClave }) {
                                Text(if (verClave) "Ocultar" else "Ver")
                            }
                        },
                        supportingText = errorClave?.let { mensaje ->
                            { Text(mensaje) }
                        },
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