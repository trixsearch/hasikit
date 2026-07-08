package com.trixsearch.hasikit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme { SYSTEM, LIGHT, DARK }

// Amazon Prime Video dark theme
private val DarkColorScheme = darkColorScheme(
    primary = PrimeBlue,
    onPrimary = Color.White,
    primaryContainer = PrimeDarkBlue,
    onPrimaryContainer = PrimeWhite,
    secondary = PrimeLightBlue,
    onSecondary = PrimeDarkBg,
    tertiary = PrimeGray,
    onTertiary = Color.White,
    background = PrimeDarkBg,
    onBackground = PrimeWhite,
    surface = PrimeSurface,
    onSurface = PrimeWhite,
    surfaceVariant = PrimeSurface2,
    onSurfaceVariant = PrimeGray,
    outline = Color(0xFF3D4F5C),
    error = Color(0xFFFF6B6B),
    onError = Color.White,
)

// Amazon Prime Video light theme
private val LightColorScheme = lightColorScheme(
    primary = PrimeDarkBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F0FB),
    onPrimaryContainer = PrimeDarkBlue,
    secondary = PrimeBlue,
    onSecondary = Color.White,
    tertiary = PrimeGray,
    onTertiary = Color.White,
    background = Color(0xFFF0F2F2),
    onBackground = Color(0xFF0F171E),
    surface = Color.White,
    onSurface = Color(0xFF0F171E),
    surfaceVariant = Color(0xFFE8EDF0),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = Color(0xFFB0BEC5),
    error = Color(0xFFD32F2F),
    onError = Color.White,
)

@Composable
fun HasikitTheme(
    appTheme: AppTheme = AppTheme.DARK,
    content: @Composable () -> Unit
) {
    val useDark = when (appTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (useDark) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
