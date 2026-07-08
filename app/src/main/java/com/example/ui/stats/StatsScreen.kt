package com.example.ui.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppViewModel
import java.util.Calendar

@Composable
fun StatsScreen(viewModel: AppViewModel, onScrollDirectionChanged: (Boolean) -> Unit = {}) {
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        var previousIndex = 0
        var previousScrollOffset = 0
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                if (index > previousIndex || (index == previousIndex && offset > previousScrollOffset)) {
                    // Scrolling down
                    onScrollDirectionChanged(false)
                } else if (index < previousIndex || (index == previousIndex && offset < previousScrollOffset)) {
                    // Scrolling up
                    onScrollDirectionChanged(true)
                }
                previousIndex = index
                previousScrollOffset = offset
            }
    }

    val totalSolved = sessions.sumOf { it.questionsSolved }
    
    val varcCount = sessions.filter { it.section == "VARC" }.sumOf { it.questionsSolved }
    val lrdiCount = sessions.filter { it.section == "LRDI" }.sumOf { it.questionsSolved }
    val quantCount = sessions.filter { it.section == "Quant" }.sumOf { it.questionsSolved }

    val today = Calendar.getInstance()
    
    val todaySolved = sessions.filter { 
        val cal = Calendar.getInstance().apply { timeInMillis = it.date }
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }.sumOf { it.questionsSolved }

    val thisWeekSolved = sessions.filter { 
        val cal = Calendar.getInstance().apply { timeInMillis = it.date }
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        cal.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR)
    }.sumOf { it.questionsSolved }

    val thisMonthSolved = sessions.filter { 
        val cal = Calendar.getInstance().apply { timeInMillis = it.date }
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        cal.get(Calendar.MONTH) == today.get(Calendar.MONTH)
    }.sumOf { it.questionsSolved }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.fadeIn(animationSpec = tween(300)) + androidx.compose.animation.slideInVertically(animationSpec = tween(300), initialOffsetY = { 50 })
            ) {
                Column {
                    Text(
                        text = "Statistics",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Overall Stats
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TOTAL SOLVED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var animatedTotal by remember { mutableIntStateOf(0) }
                    LaunchedEffect(totalSolved) { animatedTotal = totalSolved }
                    val displayedTotal by animateIntAsState(targetValue = animatedTotal, animationSpec = tween(500), label = "total")
                    
                    Text(
                        text = displayedTotal.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Time Based Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeStatCard(title = "Today", value = todaySolved.toString(), modifier = Modifier.weight(1f))
                TimeStatCard(title = "This Week", value = thisWeekSolved.toString(), modifier = Modifier.weight(1f))
                TimeStatCard(title = "This Month", value = thisMonthSolved.toString(), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Section Stats
            Text(
                text = "By Section",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            SectionProgress(
                section = "VARC",
                count = varcCount,
                total = totalSolved,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            SectionProgress(
                section = "LRDI",
                count = lrdiCount,
                total = totalSolved,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            SectionProgress(
                section = "Quant",
                count = quantCount,
                total = totalSolved,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Recent Sessions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        val recentSessions = sessions.take(5)
        if (recentSessions.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ListAlt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No sessions yet.\nStart solving questions!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(recentSessions.size) { index ->
                val session = recentSessions[index]
                var itemVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay((index * 50).toLong())
                    itemVisible = true
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = itemVisible,
                    enter = androidx.compose.animation.fadeIn(animationSpec = tween(300)) + androidx.compose.animation.slideInVertically(animationSpec = tween(300), initialOffsetY = { 50 })
                ) {
                    Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
                            Text(
                                text = dateFormat.format(java.util.Date(session.date)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = session.section,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "+${session.questionsSolved}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                }
            }
        }
    }
}

@Composable
fun TimeStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    var animatedValue by remember { mutableIntStateOf(0) }
    LaunchedEffect(value) { animatedValue = value.toIntOrNull() ?: 0 }
    val displayedValue by animateIntAsState(targetValue = animatedValue, animationSpec = tween(500), label = "timeStat")

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = displayedValue.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SectionProgress(section: String, count: Int, total: Int, color: androidx.compose.ui.graphics.Color) {
    val percentage = if (total > 0) ((count.toFloat() / total.toFloat()) * 100).toInt() else 0
    val progress = if (total > 0) count.toFloat() / total.toFloat() else 0f
    
    var animatedProgress by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    LaunchedEffect(progress) { animatedProgress = progress }
    val displayedProgress by animateFloatAsState(targetValue = animatedProgress, animationSpec = tween(700), label = "progress")

    var animatedCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(count) { animatedCount = count }
    val displayedCount by animateIntAsState(targetValue = animatedCount, animationSpec = tween(700), label = "count")
    val displayedPercentage = if (total > 0) ((displayedCount.toFloat() / total.toFloat()) * 100).toInt() else 0

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = section,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$displayedCount ($displayedPercentage%)",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { displayedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
