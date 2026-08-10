package com.example.lifttracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6F4E37),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEBD9C5),
    onPrimaryContainer = Color(0xFF3D2A1E),
    secondary = Color(0xFFA98260),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E4D3),
    onSecondaryContainer = Color(0xFF3D2A1E),
    background = Color(0xFFFFFAF2),
    onBackground = Color(0xFF30251D),
    surface = Color(0xFFFFFCF7),
    onSurface = Color(0xFF30251D),
    surfaceVariant = Color(0xFFF5EBDD),
    onSurfaceVariant = Color(0xFF5D4938),
    outline = Color(0xFFD8C3AC)
)

@Composable
fun LiftTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
