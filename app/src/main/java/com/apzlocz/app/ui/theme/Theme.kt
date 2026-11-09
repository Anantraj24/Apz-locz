package com.apzlocz.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

val LightOneUiColors = OneUiColors(
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurface = LightOnSurface,
    onSurfaceMuted = LightOnSurfaceMuted,
    accent = LightAccent,
    accentContainer = LightAccentContainer,
    success = LightSuccess,
    error = LightError,
    warning = LightWarning,
    isDark = false
)

val DarkOneUiColors = OneUiColors(
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurface = DarkOnSurface,
    onSurfaceMuted = DarkOnSurfaceMuted,
    accent = DarkAccent,
    accentContainer = DarkAccentContainer,
    success = DarkSuccess,
    error = DarkError,
    warning = DarkWarning,
    isDark = true
)

val LocalOneUiColors = staticCompositionLocalOf { LightOneUiColors }
val LocalOneUiTypography = staticCompositionLocalOf { OneUiTypography() }
val LocalOneUiShapes = staticCompositionLocalOf { OneUiShapes() }
val LocalOneUiSpacing = staticCompositionLocalOf { OneUiSpacing() }

object OneUiTheme {
    val colors: OneUiColors
        @Composable
        @ReadOnlyComposable
        get() = LocalOneUiColors.current

    val typography: OneUiTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalOneUiTypography.current

    val shapes: OneUiShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalOneUiShapes.current

    val spacing: OneUiSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalOneUiSpacing.current

    val motion: OneUiMotion
        get() = OneUiMotion
}

@Composable
fun ApzLoczTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val oneUiColors = if (darkTheme) DarkOneUiColors else LightOneUiColors
    val oneUiTypography = OneUiTypography()
    val oneUiShapes = OneUiShapes()
    val oneUiSpacing = OneUiSpacing()

    val materialColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = oneUiColors.accent,
            onPrimary = LightOnSurface,
            primaryContainer = oneUiColors.accentContainer,
            background = oneUiColors.background,
            surface = oneUiColors.surface,
            onBackground = oneUiColors.onSurface,
            onSurface = oneUiColors.onSurface,
            error = oneUiColors.error
        )
    } else {
        lightColorScheme(
            primary = oneUiColors.accent,
            onPrimary = DarkOnSurface,
            primaryContainer = oneUiColors.accentContainer,
            background = oneUiColors.background,
            surface = oneUiColors.surface,
            onBackground = oneUiColors.onSurface,
            onSurface = oneUiColors.onSurface,
            error = oneUiColors.error
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = oneUiColors.background.toArgb()
            window.navigationBarColor = oneUiColors.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalOneUiColors provides oneUiColors,
        LocalOneUiTypography provides oneUiTypography,
        LocalOneUiShapes provides oneUiShapes,
        LocalOneUiSpacing provides oneUiSpacing
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            content = content
        )
    }
}
