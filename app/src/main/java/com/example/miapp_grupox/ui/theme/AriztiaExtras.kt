package com.example.miapp_grupox.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Rojo más claro para todos los botones (pedido por la profesora)
val AriztiaButtonRed = Color(0xFFEF5350)

// Combo box: campo de solo lectura que al tocarlo abre una lista de opciones
@Composable
fun AriztiaDropdown(
    label: String,
    opciones: List<String>,
    seleccion: String,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = seleccion,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Text("▼") },
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )

        // Capa transparente encima para detectar el toque y abrir el menú
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expandido = true }
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

// Botón con el rojo claro de los botones de la app
@Composable
fun AriztiaLightButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorFondo: Color = AriztiaButtonRed
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = Color.White
        )
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold)
    }
}