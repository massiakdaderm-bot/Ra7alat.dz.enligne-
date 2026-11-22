package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalAppThemePalette = staticCompositionLocalOf { AppThemePalette.WHITE_ROYAL }

@Composable
fun Ra7alatTheme(
    palette: AppThemePalette = AppThemePalette.WHITE_ROYAL,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (palette.isLight) {
        androidx.compose.material3.lightColorScheme(
            primary = palette.primaryColor,
            onPrimary = Color.White,
            primaryContainer = palette.containerColor,
            onPrimaryContainer = palette.primaryDark,
            secondary = palette.accentColor,
            onSecondary = Color.White,
            secondaryContainer = palette.containerColor,
            onSecondaryContainer = palette.primaryDark,
            background = palette.darkCanvasBg,
            onBackground = palette.textPrimary,
            surface = palette.darkCardBg,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.darkSurfaceBg,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.darkBorderColor
        )
    } else {
        darkColorScheme(
            primary = palette.primaryLight,
            onPrimary = Color.White,
            primaryContainer = palette.darkSurfaceBg,
            onPrimaryContainer = palette.primaryLight,
            secondary = palette.accentColor,
            onSecondary = Color.Black,
            secondaryContainer = palette.primaryColor,
            onSecondaryContainer = Color.White,
            background = palette.darkCanvasBg,
            onBackground = Color.White,
            surface = palette.darkCardBg,
            onSurface = Color.White,
            surfaceVariant = palette.darkSurfaceBg,
            onSurfaceVariant = palette.mutedTextColor,
            outline = palette.darkBorderColor
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = palette.darkCanvasBg.toArgb()
            window.navigationBarColor = palette.darkCanvasBg.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = palette.isLight
            insetsController.isAppearanceLightNavigationBars = palette.isLight
        }
    }

    CompositionLocalProvider(LocalAppThemePalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
