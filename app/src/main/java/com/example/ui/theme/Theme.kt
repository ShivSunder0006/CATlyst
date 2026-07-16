package com.example.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class AppThemeType {
    PASTEL, NEON, VIBRANT, MIDNIGHT
}

private val PastelLightColorScheme = lightColorScheme(
    primary = PastelPrimary, onPrimary = PastelTextPrimary,
    primaryContainer = PastelPrimary, onPrimaryContainer = PastelTextPrimary,
    secondary = PastelSecondary, onSecondary = PastelTextPrimary,
    secondaryContainer = PastelSecondary, onSecondaryContainer = PastelTextPrimary,
    tertiary = PastelTertiary, onTertiary = PastelTextPrimary,
    background = PastelBackground, onBackground = PastelTextPrimary,
    surface = PastelSurface, onSurface = PastelTextPrimary,
    surfaceVariant = PastelSurfaceVariant, onSurfaceVariant = PastelTextSecondary,
)

private val PastelDarkColorScheme = darkColorScheme(
    primary = PastelPrimary, onPrimary = PastelTextPrimary,
    primaryContainer = PastelPrimary, onPrimaryContainer = PastelTextPrimary,
    secondary = PastelSecondary, onSecondary = PastelTextPrimary,
    secondaryContainer = PastelSecondary, onSecondaryContainer = PastelTextPrimary,
    tertiary = PastelTertiary, onTertiary = PastelTextPrimary,
    background = PastelDarkBackground, onBackground = PastelDarkTextPrimary,
    surface = PastelDarkSurface, onSurface = PastelDarkTextPrimary,
    surfaceVariant = PastelDarkSurfaceVariant, onSurfaceVariant = PastelDarkTextPrimary,
)

private val NeonColorScheme = darkColorScheme(
    primary = NeonPrimary, onPrimary = NeonBackground,
    primaryContainer = NeonPrimary, onPrimaryContainer = NeonBackground,
    secondary = NeonSecondary, onSecondary = NeonBackground,
    secondaryContainer = NeonSecondary, onSecondaryContainer = NeonBackground,
    tertiary = NeonTertiary, onTertiary = NeonBackground,
    background = NeonBackground, onBackground = NeonTextPrimary,
    surface = NeonSurface, onSurface = NeonTextPrimary,
    surfaceVariant = NeonSurfaceVariant, onSurfaceVariant = NeonTextSecondary,
)

private val VibrantLightColorScheme = lightColorScheme(
    primary = VibrantPrimary, onPrimary = VibrantSurface,
    primaryContainer = VibrantPrimary, onPrimaryContainer = VibrantSurface,
    secondary = VibrantSecondary, onSecondary = VibrantSurface,
    secondaryContainer = VibrantSecondary, onSecondaryContainer = VibrantSurface,
    tertiary = VibrantTertiary, onTertiary = VibrantTextPrimary,
    background = VibrantBackground, onBackground = VibrantTextPrimary,
    surface = VibrantSurface, onSurface = VibrantTextPrimary,
    surfaceVariant = VibrantSurfaceVariant, onSurfaceVariant = VibrantTextSecondary,
)

private val VibrantDarkColorScheme = darkColorScheme(
    primary = VibrantPrimary, onPrimary = VibrantDarkSurface,
    primaryContainer = VibrantPrimary, onPrimaryContainer = VibrantDarkSurface,
    secondary = VibrantSecondary, onSecondary = VibrantDarkSurface,
    secondaryContainer = VibrantSecondary, onSecondaryContainer = VibrantDarkSurface,
    tertiary = VibrantTertiary, onTertiary = VibrantDarkSurface,
    background = VibrantDarkBackground, onBackground = VibrantDarkTextPrimary,
    surface = VibrantDarkSurface, onSurface = VibrantDarkTextPrimary,
    surfaceVariant = VibrantDarkSurfaceVariant, onSurfaceVariant = VibrantDarkTextSecondary,
)

private val MidnightColorScheme = darkColorScheme(
    primary = MidnightPrimary, onPrimary = MidnightBackground,
    primaryContainer = MidnightPrimary, onPrimaryContainer = MidnightBackground,
    secondary = MidnightSecondary, onSecondary = MidnightBackground,
    secondaryContainer = MidnightSecondary, onSecondaryContainer = MidnightBackground,
    tertiary = MidnightTertiary, onTertiary = MidnightBackground,
    background = MidnightBackground, onBackground = MidnightTextPrimary,
    surface = MidnightSurface, onSurface = MidnightTextPrimary,
    surfaceVariant = MidnightSurfaceVariant, onSurfaceVariant = MidnightTextSecondary,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeType: AppThemeType = AppThemeType.PASTEL,
    isReducedMotion: Boolean = false,
    content: @Composable () -> Unit,
) {
    val targetColorScheme = when (themeType) {
        AppThemeType.PASTEL -> if (darkTheme) PastelDarkColorScheme else PastelLightColorScheme
        AppThemeType.NEON -> NeonColorScheme
        AppThemeType.VIBRANT -> if (darkTheme) VibrantDarkColorScheme else VibrantLightColorScheme
        AppThemeType.MIDNIGHT -> MidnightColorScheme
    }

    val colorScheme = if (isReducedMotion) {
        targetColorScheme
    } else {
        val animSpec = tween<Color>(300)
        ColorScheme(
            primary = animateColorAsState(targetColorScheme.primary, animSpec, label = "").value,
            onPrimary = animateColorAsState(targetColorScheme.onPrimary, animSpec, label = "").value,
            primaryContainer = animateColorAsState(targetColorScheme.primaryContainer, animSpec, label = "").value,
            onPrimaryContainer = animateColorAsState(targetColorScheme.onPrimaryContainer, animSpec, label = "").value,
            inversePrimary = animateColorAsState(targetColorScheme.inversePrimary, animSpec, label = "").value,
            secondary = animateColorAsState(targetColorScheme.secondary, animSpec, label = "").value,
            onSecondary = animateColorAsState(targetColorScheme.onSecondary, animSpec, label = "").value,
            secondaryContainer = animateColorAsState(targetColorScheme.secondaryContainer, animSpec, label = "").value,
            onSecondaryContainer = animateColorAsState(targetColorScheme.onSecondaryContainer, animSpec, label = "").value,
            tertiary = animateColorAsState(targetColorScheme.tertiary, animSpec, label = "").value,
            onTertiary = animateColorAsState(targetColorScheme.onTertiary, animSpec, label = "").value,
            tertiaryContainer = animateColorAsState(targetColorScheme.tertiaryContainer, animSpec, label = "").value,
            onTertiaryContainer = animateColorAsState(targetColorScheme.onTertiaryContainer, animSpec, label = "").value,
            background = animateColorAsState(targetColorScheme.background, animSpec, label = "").value,
            onBackground = animateColorAsState(targetColorScheme.onBackground, animSpec, label = "").value,
            surface = animateColorAsState(targetColorScheme.surface, animSpec, label = "").value,
            onSurface = animateColorAsState(targetColorScheme.onSurface, animSpec, label = "").value,
            surfaceVariant = animateColorAsState(targetColorScheme.surfaceVariant, animSpec, label = "").value,
            onSurfaceVariant = animateColorAsState(targetColorScheme.onSurfaceVariant, animSpec, label = "").value,
            surfaceTint = animateColorAsState(targetColorScheme.surfaceTint, animSpec, label = "").value,
            inverseSurface = animateColorAsState(targetColorScheme.inverseSurface, animSpec, label = "").value,
            inverseOnSurface = animateColorAsState(targetColorScheme.inverseOnSurface, animSpec, label = "").value,
            error = animateColorAsState(targetColorScheme.error, animSpec, label = "").value,
            onError = animateColorAsState(targetColorScheme.onError, animSpec, label = "").value,
            errorContainer = animateColorAsState(targetColorScheme.errorContainer, animSpec, label = "").value,
            onErrorContainer = animateColorAsState(targetColorScheme.onErrorContainer, animSpec, label = "").value,
            outline = animateColorAsState(targetColorScheme.outline, animSpec, label = "").value,
            outlineVariant = animateColorAsState(targetColorScheme.outlineVariant, animSpec, label = "").value,
            scrim = animateColorAsState(targetColorScheme.scrim, animSpec, label = "").value,
            surfaceBright = animateColorAsState(targetColorScheme.surfaceBright, animSpec, label = "").value,
            surfaceDim = animateColorAsState(targetColorScheme.surfaceDim, animSpec, label = "").value,
            surfaceContainer = animateColorAsState(targetColorScheme.surfaceContainer, animSpec, label = "").value,
            surfaceContainerHigh = animateColorAsState(targetColorScheme.surfaceContainerHigh, animSpec, label = "").value,
            surfaceContainerHighest = animateColorAsState(targetColorScheme.surfaceContainerHighest, animSpec, label = "").value,
            surfaceContainerLow = animateColorAsState(targetColorScheme.surfaceContainerLow, animSpec, label = "").value,
            surfaceContainerLowest = animateColorAsState(targetColorScheme.surfaceContainerLowest, animSpec, label = "").value,
        )
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
