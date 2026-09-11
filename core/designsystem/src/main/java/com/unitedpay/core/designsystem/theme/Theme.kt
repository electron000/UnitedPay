package com.unitedpay.core.designsystem.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Enterprise Light Theme for United Pay UPI.
 * Formally decoupled from system dark mode to prevent contrast/inversion bugs
 * and ensure strict compliance with banking & fintech light aesthetic standards.
 */
private val LightColorScheme = lightColorScheme(
    primary = UnitedMoneyBlue,
    onPrimary = UnitedWhite,
    primaryContainer = UnitedSurfaceSubtle,
    onPrimaryContainer = UnitedDeepBlue,
    secondary = UnitedShieldCyan,
    onSecondary = UnitedWhite,
    background = UnitedBackgroundLight,
    onBackground = UnitedTextPrimary,
    surface = UnitedWhite,
    onSurface = UnitedTextPrimary,
    surfaceVariant = UnitedSurfaceSubtle,
    onSurfaceVariant = UnitedTextSecondary,
    outline = UnitedBorderLight,
    error = UnitedError,
    onError = UnitedWhite
)

@Composable
fun UnitedPayTheme(
    darkTheme: Boolean = false, // Strictly false: platform is purely light-themed
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = UnitedMoneyBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = UnitedTypography,
        content = content
    )
}
