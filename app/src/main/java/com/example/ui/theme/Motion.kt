package com.example.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppMotion {
    // Durations
    const val DurationQuick = 150
    const val DurationStandard = 250
    const val DurationScreen = 250
    const val DurationTheme = 350
    const val DurationSuccess = 450

    // Easings
    val StandardEasing = FastOutSlowInEasing
    val DecelerateEasing = LinearOutSlowInEasing

    // Specs
    fun <T> quick() = tween<T>(durationMillis = DurationQuick, easing = StandardEasing)
    fun <T> standard() = tween<T>(durationMillis = DurationStandard, easing = StandardEasing)
    fun <T> screen() = tween<T>(durationMillis = DurationScreen, easing = DecelerateEasing)
    fun <T> theme() = tween<T>(durationMillis = DurationTheme, easing = StandardEasing)
    fun <T> success() = spring<T>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    fun <T> subtleSpring() = spring<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
}

object AppSpacing {
    val ExtraSmall: Dp = 4.dp
    val Small: Dp = 8.dp
    val Medium: Dp = 16.dp
    val Large: Dp = 24.dp
    val ExtraLarge: Dp = 32.dp
}
