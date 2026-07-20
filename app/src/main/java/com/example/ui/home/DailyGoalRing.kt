package com.example.ui.home

import com.example.ui.theme.AppMotion

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DailyGoalRing(dailyProgress: Float, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = dailyProgress,
        animationSpec = AppMotion.success(),
        label = "dailyProgressAnimation"
    )

    var goalReached by remember { mutableStateOf(false) }
    var showShimmer by remember { mutableStateOf(false) }

    LaunchedEffect(dailyProgress) {
        if (dailyProgress >= 1f && !goalReached) {
            goalReached = true
            showShimmer = true
        } else if (dailyProgress < 1f) {
            goalReached = false
            showShimmer = false
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onBackground = MaterialTheme.colorScheme.onBackground

    val shimmerTransition = rememberInfiniteTransition(label = "shimmerTransition")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(40.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 4.dp.toPx()
            
            // Background ring
            drawArc(
                color = surfaceVariant,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress ring
            if (showShimmer && animatedProgress >= 1f) {
                val gradient = Brush.linearGradient(
                    colors = listOf(primaryColor, Color.White, primaryColor),
                    start = Offset(size.width * shimmerOffset, size.height * shimmerOffset),
                    end = Offset(size.width * (shimmerOffset + 0.5f), size.height * (shimmerOffset + 0.5f))
                )
                drawArc(
                    brush = gradient,
                    startAngle = -90f,
                    sweepAngle = (animatedProgress.coerceIn(0f, 1f)) * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            } else {
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = (animatedProgress.coerceIn(0f, 1f)) * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
        
        Text(
            "${(animatedProgress.coerceIn(0f, 1f) * 100).toInt()}%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = onBackground
        )
    }
}
