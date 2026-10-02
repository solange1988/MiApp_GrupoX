
package miapp_grupox.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontSize = 23.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    ),
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontSize = 19.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontSize = 15.sp,
        color = TextPrimary
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontSize = 13.sp,
        color = TextSecondary
    ),
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
)