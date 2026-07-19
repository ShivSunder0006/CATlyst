package com.example.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppViewModel
import com.example.ui.HomeUiState
import java.util.Calendar

@Composable
fun Modifier.bounceClick(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(24.dp)
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, spring(), label = "bounceScale")
    return this.scale(scale)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val haptic = LocalHapticFeedback.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val isReducedMotion = remember(context) {
        android.provider.Settings.Global.getFloat(
            context.contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    }
    
    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val totalSolved by viewModel.totalSolved.collectAsStateWithLifecycle()
    val todaySolved by viewModel.todaySolved.collectAsStateWithLifecycle()

    var showGoalPicker by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    if (showGoalPicker) {
        GoalPickerSheet(
            currentGoal = uiState.goal,
            onDismiss = { showGoalPicker = false },
            onGoalSelected = { 
                viewModel.updateGoal(it)
                showGoalPicker = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        
        val dailyGoal by viewModel.dailyGoal.collectAsStateWithLifecycle()
        val dailyProgress = if (dailyGoal > 0) (todaySolved.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Today's Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            DailyGoalRing(dailyProgress = dailyProgress)
        }

        // Section Selector
        val sections = listOf("Quant", "VARC", "LRDI")
        var dragOffset by remember { mutableStateOf(0f) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffset > 50) {
                                val currentIndex = sections.indexOf(uiState.selectedSection)
                                if (currentIndex > 0) {
                                    viewModel.updateSection(sections[currentIndex - 1])
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            } else if (dragOffset < -50) {
                                val currentIndex = sections.indexOf(uiState.selectedSection)
                                if (currentIndex < sections.size - 1) {
                                    viewModel.updateSection(sections[currentIndex + 1])
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            }
                            dragOffset = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            dragOffset += dragAmount
                            change.consume()
                        }
                    )
                }
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            sections.forEach { section ->
                val isSelected = uiState.selectedSection == section
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.secondaryContainer 
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                        .border(
                            width = if (isSelected) 0.dp else 1.dp,
                            color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        )
                        .clickable { viewModel.updateSection(section) }
                        .semantics {
                            selected = isSelected
                            role = Role.Tab
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = section,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Summary Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(title = "Total", value = totalSolved.toString(), modifier = Modifier.weight(1f))
            StatCard(title = "Today", value = todaySolved.toString(), modifier = Modifier.weight(1f))
        }

        // Central Counter & Progress
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = "Change target",
                        onClick = { 
                            showGoalPicker = true 
                            if (!uiState.hintSeen) viewModel.dismissHint()
                        }
                    )
                    .semantics {
                        val g = uiState.goal
                        val targetDesc = if (g != null && g > 0) "Target $g." else "No target set."
                        contentDescription = "${uiState.currentSessionCount} questions completed. $targetDesc Double tap to change target."
                        role = Role.Button
                    },
                contentAlignment = Alignment.Center
            ) {
                val goal = uiState.goal
                val count = uiState.currentSessionCount
                val targetProgress = if (goal != null && goal > 0) count.toFloat() / goal.toFloat() else 0f
                
                var showCompletion by remember { mutableStateOf(false) }
                LaunchedEffect(count, goal) {
                    if (goal != null && goal > 0 && count == goal && targetProgress == 1f) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showCompletion = true
                    }
                }
                
                val progressGradient = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = if (showCompletion && targetProgress >= 1f) 
                        listOf(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.primary)
                    else 
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                )
                val trackColor = MaterialTheme.colorScheme.surfaceVariant
                
                val animatedProgress by animateFloatAsState(targetValue = targetProgress, animationSpec = tween(500), label = "circularProgress")
                
                val errorColor = MaterialTheme.colorScheme.error
                Canvas(modifier = Modifier.size(240.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    drawCircle(
                        color = trackColor,
                        radius = size.minDimension / 2 - strokeWidth / 2,
                        style = Stroke(width = strokeWidth)
                    )
                    
                    if (animatedProgress > 0) {
                        drawArc(
                            brush = progressGradient,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress.coerceIn(0f, 1f),
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = Size(size.minDimension - strokeWidth, size.minDimension - strokeWidth),
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                        )
                    }
                    if (animatedProgress > 1f) {
                        drawArc(
                            color = errorColor,
                            startAngle = -90f,
                            sweepAngle = 360f * (animatedProgress - 1f).coerceIn(0f, 1f),
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = Size(size.minDimension - strokeWidth, size.minDimension - strokeWidth),
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                        )
                    }
                }
                
                var pulseScale by remember { mutableStateOf(1f) }
                LaunchedEffect(showCompletion) {
                    if (showCompletion) {
                        pulseScale = 1.15f
                        kotlinx.coroutines.delay(150)
                        pulseScale = 1f
                    }
                }
                val animatedPulse by animateFloatAsState(targetValue = pulseScale, animationSpec = tween(200), label = "pulse")
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(animatedPulse)
                ) {
                    AnimatedContent(
                        targetState = if (showCompletion && targetProgress >= 1f) "TARGET REACHED" else "CURRENT SESSION",
                        label = "session_title_anim"
                    ) { text ->
                        Text(
                            text = text,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (text == "TARGET REACHED") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        AnimatedContent(
                            targetState = count,
                            transitionSpec = {
                                if (isReducedMotion) {
                                    fadeIn(animationSpec = tween(150)).togetherWith(fadeOut(animationSpec = tween(150)))
                                } else if (targetState > initialState) {
                                    (slideInVertically(animationSpec = tween(150)) { height -> height } + fadeIn(animationSpec = tween(150)))
                                        .togetherWith(slideOutVertically(animationSpec = tween(150)) { height -> -height } + fadeOut(animationSpec = tween(150)))
                                } else {
                                    (slideInVertically(animationSpec = tween(150)) { height -> -height } + fadeIn(animationSpec = tween(150)))
                                        .togetherWith(slideOutVertically(animationSpec = tween(150)) { height -> height } + fadeOut(animationSpec = tween(150)))
                                }
                            },
                            label = "counter_animation"
                        ) { c ->
                            Text(
                                text = c.toString(),
                                fontSize = if (goal != null && goal > 0) 48.sp else 72.sp,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        if (goal != null && goal > 0) {
                            Text(
                                text = " / $goal",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Questions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!uiState.hintSeen) {
                    Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp).offset(y = 48.dp)) {
                        Text(
                            text = "Tap the number to set a target",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (uiState.currentSessionCount > 0 || uiState.goal != null) {
                var showDiscardDialog by remember { mutableStateOf(false) }

                if (showDiscardDialog) {
                    AlertDialog(
                        onDismissRequest = { showDiscardDialog = false },
                        title = { Text("Discard Current Session?") },
                        text = { Text("This will discard the current session and all unsaved progress.") },
                        confirmButton = {
                            Button(onClick = { showDiscardDialog = false }) {
                                Text("Keep Session")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDiscardDialog = false
                                viewModel.discardSession()
                            }) {
                                Text("Discard")
                            }
                        }
                    )
                }

                IconButton(
                    onClick = { showDiscardDialog = true },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Discard current session",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Main Controls (One-Handed Focus)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minus Button
                val minusInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .bounceClick(minusInteractionSource)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(
                            interactionSource = minusInteractionSource, 
                            indication = androidx.compose.foundation.LocalIndication.current,
                            onClickLabel = "Remove one question"
                        ) { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.decrementCounter() 
                        }
                        .semantics {
                            contentDescription = "Remove one question"
                            role = Role.Button
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.width(32.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.onPrimaryContainer))
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Plus Button
                val plusInteractionSource = remember { MutableInteractionSource() }
                var showQuickAdd by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .bounceClick(plusInteractionSource, RoundedCornerShape(40.dp))
                        .clip(RoundedCornerShape(40.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .combinedClickable(
                            interactionSource = plusInteractionSource,
                            indication = androidx.compose.foundation.LocalIndication.current,
                            onClickLabel = "Add one question",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.incrementCounter()
                            },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showQuickAdd = true
                            }
                        )
                        .semantics {
                            contentDescription = "Add one question"
                            role = Role.Button
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val iconColor = MaterialTheme.colorScheme.onPrimary
                    Canvas(modifier = Modifier.size(40.dp)) {
                        drawLine(
                            color = iconColor,
                            start = androidx.compose.ui.geometry.Offset(size.width / 2, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width / 2, size.height),
                            strokeWidth = 4.5.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        drawLine(
                            color = iconColor,
                            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
                            strokeWidth = 4.5.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }
                    androidx.compose.material3.DropdownMenu(
                        expanded = showQuickAdd,
                        onDismissRequest = { showQuickAdd = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    ) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("+5", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.incrementCounter(5)
                                showQuickAdd = false
                            }
                        )
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("+10", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.incrementCounter(10)
                                showQuickAdd = false
                            }
                        )
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("+20", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.incrementCounter(20)
                                showQuickAdd = false
                            }
                        )
                    }
                }
            }

            // Secondary Actions
            var showCheckmark by remember { mutableStateOf(false) }
            LaunchedEffect(showCheckmark) {
                if (showCheckmark) {
                    kotlinx.coroutines.delay(1000)
                    showCheckmark = false
                }
            }
            val saveInteractionSource = remember { MutableInteractionSource() }
            Button(
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.saveSession() 
                    showCheckmark = true
                },
                enabled = !showCheckmark && uiState.currentSessionCount > 0,
                interactionSource = saveInteractionSource,
                modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(saveInteractionSource, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                AnimatedContent(targetState = showCheckmark, label = "save_btn_anim") { show ->
                    if (show) {
                        Icon(Icons.Default.Check, contentDescription = "Saved")
                    } else {
                        Text("Save Session", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
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
                text = title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            var animatedValue by remember { mutableIntStateOf(0) }
            LaunchedEffect(value) { animatedValue = value.toIntOrNull() ?: 0 }
            val displayedValue by animateIntAsState(targetValue = animatedValue, animationSpec = tween(500), label = "statValue")
            
            Text(
                text = displayedValue.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun GoalPickerSheet(
    currentGoal: Int?,
    onDismiss: () -> Unit,
    onGoalSelected: (Int?) -> Unit
) {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedValue by remember(currentGoal) { mutableStateOf(currentGoal ?: 30) }
    val options = (1..40).map { it * 5 }
    
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set Session Goal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            val listState = androidx.compose.foundation.lazy.rememberLazyListState(
                initialFirstVisibleItemIndex = maxOf(0, options.indexOf(selectedValue))
            )
            val flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(lazyListState = listState)
            val itemHeight = 64.dp
            
            val paddedItems = listOf(null, null) + options + listOf(null, null)
            val haptic = LocalHapticFeedback.current
            
            androidx.compose.runtime.LaunchedEffect(listState) {
                androidx.compose.runtime.snapshotFlow { listState.firstVisibleItemIndex }
                    .collect { index ->
                        val newValue = paddedItems.getOrNull(index + 2)
                        if (newValue != null && newValue != selectedValue) {
                            selectedValue = newValue
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight * 5),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(itemHeight)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
                
                androidx.compose.foundation.lazy.LazyColumn(
                    state = listState,
                    flingBehavior = flingBehavior,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(paddedItems.size) { index ->
                        val item = paddedItems[index]
                        val isSelected = listState.firstVisibleItemIndex + 2 == index
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item != null) {
                                Text(
                                    text = item.toString(),
                                    fontSize = if (isSelected) 28.sp else 22.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight * 1.5f)
                        .align(Alignment.TopCenter)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(MaterialTheme.colorScheme.surface, androidx.compose.ui.graphics.Color.Transparent)
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight * 1.5f)
                        .align(Alignment.BottomCenter)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(androidx.compose.ui.graphics.Color.Transparent, MaterialTheme.colorScheme.surface)
                            )
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                val doneInteractionSource = remember { MutableInteractionSource() }
                Button(
                    onClick = { onGoalSelected(selectedValue) },
                    interactionSource = doneInteractionSource,
                    modifier = Modifier.bounceClick(doneInteractionSource, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text("Done")
                }
            }
        }
    }
}
