package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.AppViewModel
import com.example.ui.UiEvent
import com.example.ui.home.HomeScreen
import com.example.ui.stats.StatsScreen
import kotlinx.coroutines.launch

import com.example.ui.settings.SettingsScreen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton

import com.example.ui.home.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed && event.sessionToRestore != null) {
                        viewModel.restoreSession(event.sessionToRestore, event.isUndoSave)
                    }
                }
            }
        }
    }
    
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    var isThemeSelectorExpanded by remember { mutableStateOf(false) }
    val currentThemeString by viewModel.currentTheme.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { isThemeSelectorExpanded = !isThemeSelectorExpanded }
                                .padding(end = 8.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CAT", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text("lyst", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        
                        AnimatedVisibility(
                            visible = isThemeSelectorExpanded,
                            enter = expandHorizontally(
                                expandFrom = Alignment.Start,
                                animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(200)),
                            exit = shrinkHorizontally(
                                shrinkTowards = Alignment.Start,
                                animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing)
                            ) + fadeOut(animationSpec = tween(200))
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val themes = listOf(
                                    "PASTEL" to Color(0xFFC6E7FF),
                                    "NEON" to Color(0xFF00FFCC),
                                    "VIBRANT" to Color(0xFFFF3366),
                                    "FOREST" to Color(0xFF2E8B57)
                                )
                                
                                themes.forEach { (themeName, themeColor) ->
                                    val isSelected = currentThemeString == themeName
                                    val themeInteractionSource = remember { MutableInteractionSource() }
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .bounceClick(themeInteractionSource, CircleShape)
                                            .clip(CircleShape)
                                            .background(themeColor)
                                            .border(
                                                width = if (isSelected) 2.dp else 0.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable(
                                                interactionSource = themeInteractionSource,
                                                indication = androidx.compose.foundation.LocalIndication.current
                                            ) {
                                                viewModel.setTheme(themeName)
                                                isThemeSelectorExpanded = false
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.Black.copy(alpha = 0.5f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
                    IconButton(onClick = { 
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> HomeScreen(viewModel = viewModel)
                    1 -> StatsScreen(
                        viewModel = viewModel,
                        onScrollDirectionChanged = { }
                    )
                    2 -> SettingsScreen(viewModel = viewModel)
                }
            }

            if (isThemeSelectorExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isThemeSelectorExpanded = false }
                )
            }
        }
    }
}
