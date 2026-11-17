package com.example.miapp_grupox.ui.theme



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Rojo de los botones. La profesora pidió un rojo más claro (0xFFEF5350).
// Para usar el rojo intenso del prototipo, cambiar por: AriztiaRed
val AriztiaButtonRed = Color(0xFFEF5350)

// Forma y colores comunes de todos los campos (bordes suaves, fondo blanco)
private val FormaCampo = RoundedCornerShape(16.dp)

@Composable
private fun coloresCampo(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AriztiaRed,
    unfocusedBorderColor = BorderColor,
    focusedContainerColor = CardBackground,
    unfocusedContainerColor = CardBackground,
    focusedLabelColor = AriztiaRed,
    unfocusedLabelColor = TextSecondary,
    focusedLeadingIconColor = AriztiaRed,
    unfocusedLeadingIconColor = TextSecondary,
    cursorColor = AriztiaRed
)

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
            trailingIcon = {
                // Flecha normal; si hay error, ícono de advertencia en rojo
                if (isError) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Campo con error",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Abrir lista"
                    )
                }
            },
            isError = isError,
            shape = FormaCampo,
            colors = coloresCampo(),
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

// Campo de texto con ícono al inicio y mensaje de error por campo
@Composable
fun AriztiaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    iconoInicial: ImageVector,
    error: String? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(imageVector = iconoInicial, contentDescription = null)
        },
        trailingIcon = if (error != null) {
            {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        } else null,
        isError = error != null,
        supportingText = error?.let { mensaje -> { Text(mensaje) } },
        singleLine = singleLine,
        minLines = minLines,
        shape = FormaCampo,
        colors = coloresCampo(),
        modifier = modifier.fillMaxWidth()
    )
}

// Recuadro para tomar la foto de evidencia (estilo del prototipo)
@Composable
fun AriztiaCameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AppBackground,
            contentColor = TextPrimary
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(AriztiaLightRed, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = AriztiaRed
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tomar foto de evidencia",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "Usa la cámara del dispositivo",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// Botón con el rojo de los botones de la app
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
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = Color.White
        )
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}