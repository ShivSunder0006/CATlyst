package com.example.ui.settings

import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AppMotion
import com.example.ui.theme.AppSpacing
import com.example.ui.theme.bounceClick
import com.example.ui.theme.isReducedMotionEnabled
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val isReducedMotion = isReducedMotionEnabled()

    LaunchedEffect(Unit) {
        isVisible = true
    }

    fun dismiss() {
        if (!isVisible) return
        isVisible = false
        coroutineScope.launch {
            if (!isReducedMotion) {
                delay(AppMotion.DurationStandard.toLong())
            }
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = { dismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        )
    ) {
        val appVersion = remember(context) {
            try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                pInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                "1.0.0"
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { dismiss() }
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = if (isReducedMotion) fadeIn(animationSpec = AppMotion.standard()) else fadeIn(animationSpec = AppMotion.standard()) + scaleIn(initialScale = 0.9f, animationSpec = AppMotion.standard()),
                exit = if (isReducedMotion) fadeOut(animationSpec = AppMotion.standard()) else fadeOut(animationSpec = AppMotion.standard()) + scaleOut(targetScale = 0.9f, animationSpec = AppMotion.standard()),
                modifier = Modifier
                    .padding(AppSpacing.Large)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Intercept clicks so they don't dismiss the dialog
                    )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppSpacing.Large)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("CAT", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineMedium)
                                    Text("lyst", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineMedium)
                                }
                                Text("v$appVersion", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            val closeInteractionSource = remember { MutableInteractionSource() }
                            IconButton(
                                onClick = { dismiss() },
                                modifier = Modifier.bounceClick(closeInteractionSource).size(48.dp),
                                interactionSource = closeInteractionSource
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close About",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.Medium))

                        Text(
                            text = "Track Questions. Stay Consistent.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.Small))

                        Text(
                            text = "CATlyst is a lightweight, offline-first tracker built to help CAT aspirants focus on what matters most—consistent practice. Record your solved questions, monitor your progress, and stay motivated with a fast, distraction-free experience.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.Medium))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(AppSpacing.Medium))

                        Text(
                            text = "About the Developer",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.Small))

                        Text(
                            text = "Hi! I'm the developer behind CATlyst. I built this app with one simple goal: to create a clean, fast, and distraction-free companion for CAT aspirants. I believe great productivity tools should stay out of your way, helping you focus on consistent progress rather than unnecessary features.\n\nThank you for using CATlyst. I hope it becomes a small but meaningful part of your CAT journey. 🚀",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.Large))

                        Text(
                            text = "Made with ❤️ for CAT aspirants",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
