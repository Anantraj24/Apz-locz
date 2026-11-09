package com.apzlocz.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Light Theme Tokens
val LightBackground = Color(0xFFF4F4F6)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEBEBF0)
val LightOnSurface = Color(0xFF1A1A1A)
val LightOnSurfaceMuted = Color(0xFF6E6E73)
val LightAccent = Color(0xFF3E91FF)
val LightAccentContainer = Color(0xFFDCEBFF)
val LightSuccess = Color(0xFF34C759)
val LightError = Color(0xFFFF453A)
val LightWarning = Color(0xFFFFB020)

// Dark Theme Tokens (AMOLED true black background per docs/04)
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF1C1C1E)
val DarkSurfaceVariant = Color(0xFF2C2C2E)
val DarkOnSurface = Color(0xFFF2F2F2)
val DarkOnSurfaceMuted = Color(0xFF9A9AA0)
val DarkAccent = Color(0xFF4C9CFF)
val DarkAccentContainer = Color(0xFF16304F)
val DarkSuccess = Color(0xFF30D158)
val DarkError = Color(0xFFFF6961)
val DarkWarning = Color(0xFFFFC247)

/**
 * Custom color scheme holding the exact Samsung One UI inspired tokens
 * defined in docs/04_DESIGN_SYSTEM.md.
 */
@Immutable
data class OneUiColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val accent: Color,
    val accentContainer: Color,
    val success: Color,
    val error: Color,
    val warning: Color,
    val isDark: Boolean
)

