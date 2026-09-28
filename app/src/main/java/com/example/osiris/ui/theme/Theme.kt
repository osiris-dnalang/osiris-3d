package com.example.osiris.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = OsirisCyan,
    onPrimary = OsirisBg,
    primaryContainer = OsirisCyanDim,
    onPrimaryContainer = OsirisTextPrimary,
    secondary = OsirisEmerald,
    onSecondary = OsirisBg,
    secondaryContainer = OsirisEmeraldDim,
    onSecondaryContainer = OsirisTextPrimary,
    tertiary = OsirisViolet,
    background = OsirisBg,
    onBackground = OsirisTextPrimary,
    surface = OsirisSurface,
    onSurface = OsirisTextPrimary,
    surfaceVariant = OsirisSurfaceCard,
    onSurfaceVariant = OsirisTextSecondary,
    outline = OsirisBorder,
    error = OsirisRose,
    onError = OsirisBg
)

@Composable
fun OsirisGovernanceTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = OsirisBg.toArgb()
            window.navigationBarColor = OsirisBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
