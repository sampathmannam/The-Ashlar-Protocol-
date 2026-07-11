package com.ashlarprotocol.ui.theme

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

// The full scheme. Every role is set so that built-in Material 3 components (text fields, dialogs,
// switches, ripples, error states) render in the warm-stone palette instead of the M3 baseline
// (which is purple). The app mostly reads the raw color vals directly, but completing the scheme
// closes the gap wherever a stock component reaches for a role. Crisis red lives on `error`.
private val DarkColorScheme =
  darkColorScheme(
    primary = Gold,
    onPrimary = Charcoal,
    primaryContainer = Slate,
    onPrimaryContainer = Gold,
    secondary = Silver,
    onSecondary = Charcoal,
    secondaryContainer = Slate,
    onSecondaryContainer = LightText,
    tertiary = Gold,
    onTertiary = Charcoal,
    background = Charcoal,
    onBackground = LightText,
    surface = Surface,
    onSurface = LightText,
    surfaceVariant = Slate,
    onSurfaceVariant = Silver,
    surfaceContainerLowest = Charcoal,
    surfaceContainerLow = Surface,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = Slate,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = RedAlert,
    onError = Charcoal,
    errorContainer = Color(0xFF3A1712),
    onErrorContainer = RedAlert,
    scrim = Charcoal,
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
