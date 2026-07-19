package com.example.ui.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Delete
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: AppViewModel, onScrollDirectionChanged: (Boolean) -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isReducedMotion = remember(context) {
        android.provider.Settings.Global.getFloat(
            context.contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    }
    
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

    val totalSolved by viewModel.totalSolved.collectAsStateWithLifecycle()
    val varcCount by viewModel.varcCount.collectAsStateWithLifecycle()
    val lrdiCount by viewModel.lrdiCount.collectAsStateWithLifecycle()
    val quantCount by viewModel.quantCount.collectAsStateWithLifecycle()
    val todaySolved by viewModel.todaySolved.collectAsStateWithLifecycle()
    val thisWeekSolved by viewModel.thisWeekSolved.collectAsStateWithLifecycle()
    val thisMonthSolved by viewModel.thisMonthSolved.collectAsStateWithLifecycle()
    val lastWeekSolved by viewModel.lastWeekSolved.collectAsStateWithLifecycle()
    val last7DaysSessions by viewModel.last7DaysSessions.collectAsStateWithLifecycle()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

        val highestQuestionDay = remember(sessions) {
            sessions.groupBy { session -> 
                val cal = Calendar.getInstance().apply { timeInMillis = session.date }
                "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
            }.maxByOrNull { it.value.sumOf { s -> s.questionsSolved } }?.value?.sumOf { it.questionsSolved } ?: 0
        }
        val distinctDays = remember(sessions) {
            sessions.map { session ->
                val cal = Calendar.getInstance().apply { timeInMillis = session.date }
                "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
            }.distinct().size
        }
        val averagePerDay = if (distinctDays > 0) totalSolved / distinctDays else 0
        val mostPracticed = remember(varcCount, lrdiCount, quantCount) {
            val counts = mapOf("VARC" to varcCount, "LRDI" to lrdiCount, "Quant" to quantCount)
            val maxCount = counts.maxByOrNull { it.value }
            if (maxCount != null && maxCount.value > 0) maxCount.key else "-"
        }
        
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

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
                enter = if (isReducedMotion) androidx.compose.animation.fadeIn(animationSpec = tween(300)) 
                        else androidx.compose.animation.fadeIn(animationSpec = tween(300)) + androidx.compose.animation.slideInVertically(animationSpec = tween(300), initialOffsetY = { 50 })
            ) {
                Column {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (sessions.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No questions tracked yet.",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Save your first session to see your progress.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
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

            Spacer(modifier = Modifier.height(16.dp))

            // Insights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InsightCard(title = "Best Day", value = highestQuestionDay.toString(), modifier = Modifier.weight(1f))
                InsightCard(title = "Avg/Day", value = averagePerDay.toString(), modifier = Modifier.weight(1f))
                InsightCard(title = "Top Section", value = mostPracticed, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Time Based Stats
            val weeklyComparison = if (lastWeekSolved == 0) {
                "No previous-week data"
            } else {
                val diff = thisWeekSolved - lastWeekSolved
                val percent = (diff.toFloat() / lastWeekSolved.toFloat() * 100).toInt()
                if (diff >= 0) "↑ $percent% from last week" else "↓ ${-percent}% from last week"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeStatCard(title = "Today", value = todaySolved.toString(), modifier = Modifier.weight(1f))
                TimeStatCard(title = "This Week", value = thisWeekSolved.toString(), subtitle = weeklyComparison, modifier = Modifier.weight(1f))
                TimeStatCard(title = "This Month", value = thisMonthSolved.toString(), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 7-Day Activity
            Text(
                text = "7-Day Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            SevenDayActivityChart(last7DaysSessions)

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

                    } // End of else block
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            val filters = listOf("Today", "This Week", "This Month", "All")

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by section...") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            modifier = Modifier
                                .clickable { selectedFilter = filter }
                                .clip(RoundedCornerShape(16.dp)),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = filter,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        val filteredSessions = sessions.filter { session ->
            val matchesSearch = session.section.contains(searchQuery, ignoreCase = true)
            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            val currentYear = cal.get(Calendar.YEAR)
            val currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR)
            val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)
            val currentMonth = cal.get(Calendar.MONTH)

            cal.timeInMillis = session.date
            val sessionYear = cal.get(Calendar.YEAR)
            val sessionDayOfYear = cal.get(Calendar.DAY_OF_YEAR)
            val sessionWeek = cal.get(Calendar.WEEK_OF_YEAR)
            val sessionMonth = cal.get(Calendar.MONTH)

            val matchesFilter = when (selectedFilter) {
                "Today" -> sessionYear == currentYear && sessionDayOfYear == currentDayOfYear
                "This Week" -> sessionYear == currentYear && sessionWeek == currentWeek
                "This Month" -> sessionYear == currentYear && sessionMonth == currentMonth
                else -> true
            }
            matchesSearch && matchesFilter
        }

        if (filteredSessions.isEmpty()) {
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
                        text = "No saved sessions yet.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your completed study sessions will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(
                count = filteredSessions.size,
                key = { index -> filteredSessions[index].id }
            ) { index ->
                val session = filteredSessions[index]
                var itemVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay((index * 50).toLong())
                    itemVisible = true
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = itemVisible,
                    modifier = Modifier.animateItem(),
                    enter = if (isReducedMotion) androidx.compose.animation.fadeIn(animationSpec = tween(300)) 
                            else androidx.compose.animation.fadeIn(animationSpec = tween(300)) + androidx.compose.animation.slideInVertically(animationSpec = tween(300), initialOffsetY = { 50 })
                ) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.deleteSession(session)
                                true
                            } else false
                        }
                    )
                    
                    LaunchedEffect(session) {
                        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                        }
                    }
                    var showEditSheet by remember { mutableStateOf(false) }

                    if (showEditSheet) {
                        EditSessionSheet(
                            session = session,
                            onDismiss = { showEditSheet = false },
                            onSave = { viewModel.updateSession(it) },
                            onDelete = { viewModel.deleteSession(it) }
                        )
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val color = MaterialTheme.colorScheme.error
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { showEditSheet = true },
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
}

@Composable
fun TimeStatCard(title: String, value: String, subtitle: String? = null, modifier: Modifier = Modifier) {
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
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
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

@Composable
fun InsightCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
