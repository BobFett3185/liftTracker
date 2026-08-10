package com.example.lifttracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F2A44),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7ECFF),
    onPrimaryContainer = Color(0xFF0B2239),
    secondary = Color(0xFF78B9E8),
    onSecondary = Color(0xFF0B2239),
    secondaryContainer = Color(0xFFE7F4FF),
    onSecondaryContainer = Color(0xFF0B2239),
    background = Color(0xFFF7FBFF),
    onBackground = Color(0xFF102030),
    surface = Color.White,
    onSurface = Color(0xFF102030),
    surfaceVariant = Color(0xFFEAF4FC),
    onSurfaceVariant = Color(0xFF274158),
    outline = Color(0xFFB7CBDC)
)

@Composable
fun LiftTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
