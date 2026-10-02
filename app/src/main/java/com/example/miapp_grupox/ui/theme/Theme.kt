
package miapp_grupox.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AriztiaColorScheme = lightColorScheme(
    primary = AriztiaRed,
    onPrimary = CardBackground,
    secondary = EstadoNormal,
    background = AppBackground,
    surface = CardBackground,
    onSurface = TextPrimary,
    error = EstadoCritico
)

@Composable
fun MiAppGrupoXTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AriztiaColorScheme,
        typography = AppTypography,
        content = content
    )
}