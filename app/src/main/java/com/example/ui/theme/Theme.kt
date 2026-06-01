package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TechCyan,
    onPrimary = Color.White,
    secondary = TechCyanSecondary,
    onSecondary = Color.White,
    tertiary = TechEmerald,
    onTertiary = Color.White,
    background = SlateDark,
    onBackground = SlateHeading,
    surface = SlateSurface,
    onSurface = SlateHeading,
    inverseSurface = SlateSurfaceLight,
    error = Color(0xFFEF4444)
)

private val LightColorScheme = lightColorScheme(
    primary = TechCyan,
    onPrimary = Color.White,
    secondary = TechCyanSecondary,
    onSecondary = Color.White,
    tertiary = TechEmerald,
    onTertiary = Color.White,
    background = SlateDark,
    onBackground = SlateHeading,
    surface = SlateSurface,
    onSurface = SlateHeading,
    inverseSurface = SlateSurfaceLight,
    error = Color(0xFFEF4444)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to false to apply the sparkling "Professional Polish" Light theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
