package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MahadiDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberBackgroundDark,
    primaryContainer = CyberSurfaceLight,
    onPrimaryContainer = NeonCyan,
    secondary = NeonPurple,
    onSecondary = TextWhite,
    secondaryContainer = CyberSurface,
    onSecondaryContainer = NeonViolet,
    tertiary = NeonMagenta,
    onTertiary = TextWhite,
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = CyberSurfaceBorder
)

@Composable
fun MahadiAiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MahadiDarkColorScheme,
        typography = Typography,
        content = content
    )
}
