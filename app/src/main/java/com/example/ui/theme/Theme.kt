package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = StudioRed,
    onPrimary = Color.White,
    primaryContainer = StudioRedDark,
    onPrimaryContainer = Color.White,
    secondary = StudioAmber,
    onSecondary = Color.Black,
    secondaryContainer = StudioContainerHigh,
    onSecondaryContainer = StudioAmberLight,
    tertiary = StudioBlue,
    onTertiary = Color.White,
    background = StudioCanvasDark,
    onBackground = StudioTextPrimary,
    surface = StudioSurfaceDark,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceElevated,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioCardBorder,
    outlineVariant = StudioContainerLow
)

private val LightColorScheme = lightColorScheme(
    primary = StudioRedDark,
    onPrimary = Color.White,
    primaryContainer = StudioRedLight,
    onPrimaryContainer = Color.White,
    secondary = StudioAmber,
    onSecondary = Color.Black,
    tertiary = StudioBlue,
    onTertiary = Color.White,
    background = StudioCanvasLight,
    onBackground = StudioTextPrimaryLight,
    surface = StudioSurfaceLight,
    onSurface = StudioTextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = StudioTextSecondaryLight,
    outline = StudioCardBorderLight,
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek studio dark theme per PRD vibe
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
