
package miapp_grupox.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AriztiaHeader(
    titulo: String,
    subtitulo: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AriztiaRed)
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Text(
            text = "ARIZTÍA",
            fontSize = 25.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = titulo,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitulo,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun AriztiaSectionTitle(
    titulo: String,
    descripcion: String = ""
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge
        )

        if (descripcion.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EstadoBadge(estado: String) {
    val colorFondo: Color
    val colorTexto: Color

    when (estado.trim().lowercase()) {
        "crítico", "critico" -> {
            colorFondo = EstadoCriticoBackground
            colorTexto = EstadoCritico
        }

        "advertencia" -> {
            colorFondo = EstadoAdvertenciaBackground
            colorTexto = EstadoAdvertenciaTexto
        }

        else -> {
            colorFondo = EstadoNormalBackground
            colorTexto = EstadoNormal
        }
    }

    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = estado.uppercase(),
            color = colorTexto,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            )
        )
    }
}

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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(34.dp)
                    .height(4.dp)
                    .background(
                        color,
                        RoundedCornerShape(50.dp)
                    )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = valor,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = titulo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descripcion,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

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
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AriztiaRed
        )
    ) {
        Text(
            text = texto,
            fontWeight = FontWeight.Bold
        )
    }
}

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
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AriztiaRed
        )
    ) {
        Text(
            text = texto,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AriztiaWhiteCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}