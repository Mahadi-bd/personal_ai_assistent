package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.cyberCard(
    cornerRadius: Dp = 24.dp,
    borderColor: Color = CyberSurfaceBorder,
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = CyberSurfaceGlass
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    val sheen = Brush.verticalGradient(listOf(Color(0x14FFFFFF), Color(0x04FFFFFF)))
    return this
        .clip(shape)
        .background(backgroundColor, shape)
        .background(sheen, shape)
        .border(borderWidth, borderColor, shape)
}

fun Modifier.neonCard(
    cornerRadius: Dp = 16.dp,
    glowColor: Color = NeonCyan
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    val gradient = Brush.linearGradient(
        colors = listOf(
            glowColor.copy(alpha = 0.6f),
            glowColor.copy(alpha = 0.1f),
            NeonPurple.copy(alpha = 0.3f)
        )
    )
    return this
        .clip(shape)
        .background(CyberSurfaceGlass, shape)
        .border(1.5.dp, gradient, shape)
}

val HologramRaysGradient = Brush.verticalGradient(
    colors = listOf(
        NeonCyan.copy(alpha = 0.35f),
        NeonBlue.copy(alpha = 0.15f),
        Color.Transparent
    )
)

val PedestalGlowGradient = Brush.radialGradient(
    colors = listOf(
        NeonCyan.copy(alpha = 0.8f),
        NeonPurple.copy(alpha = 0.4f),
        Color.Transparent
    )
)

val CyberButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        NeonCyan,
        NeonBlue
    )
)

val PersonaPillGradient = Brush.horizontalGradient(
    colors = listOf(
        NeonPurple,
        NeonViolet
    )
)

/** Soft dark gradient used behind every screen. */
val AppBackgroundBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF0D1424), Color(0xFF080C14), Color(0xFF0A0F1C))
)
