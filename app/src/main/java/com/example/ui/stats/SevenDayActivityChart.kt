package com.example.ui.stats

import com.example.ui.theme.AppSpacing

import com.example.ui.theme.AppMotion

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.Session
import java.util.Calendar

@Composable
fun SevenDayActivityChart(sessions: List<Session>, modifier: Modifier = Modifier) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val todayStart = calendar.timeInMillis

    val days = (6 downTo 0).map { offset ->
        val dayCal = Calendar.getInstance().apply {
            timeInMillis = todayStart
            add(Calendar.DAY_OF_YEAR, -offset)
        }
        dayCal
    }

    val dayNames = listOf("S", "M", "T", "W", "T", "F", "S")
    val data = days.map { dayCal ->
        val dayStart = dayCal.timeInMillis
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        sessions.filter { it.date in dayStart..dayEnd }.sumOf { it.questionsSolved }
    }

    val maxData = data.maxOrNull()?.coerceAtLeast(10) ?: 10

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val barColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.Bottom
        ) {
            data.forEach { value ->
                val targetHeight = if (isVisible) value.toFloat() / maxData.toFloat() else 0f
                val animatedHeight by animateFloatAsState(
                    targetValue = targetHeight,
                    animationSpec = AppMotion.success(),
                    label = "barHeight"
                )
                
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = AppSpacing.ExtraSmall)
                ) {
                    val cornerRadius = CornerRadius(16.dp.toPx())
                    drawRoundRect(
                        color = trackColor,
                        size = size,
                        cornerRadius = cornerRadius
                    )
                    
                    val barHeight = size.height * animatedHeight
                    if (barHeight > 0) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(0f, size.height - barHeight),
                            size = Size(size.width, barHeight),
                            cornerRadius = cornerRadius
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { dayCal ->
                val dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK)
                Text(
                    text = dayNames[dayOfWeek - 1],
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
