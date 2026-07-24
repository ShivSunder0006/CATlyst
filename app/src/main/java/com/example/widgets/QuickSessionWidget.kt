package com.example.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import com.example.data.ActiveSessionPreferences
import kotlinx.coroutines.flow.first

class QuickSessionWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuickSessionWidget()
}

class QuickSessionWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetPrefs = WidgetPreferences(context).getConfigFlow(id.hashCode()).first()
        val corner = widgetPrefs.cornerRadius.dp
        val prefs = ActiveSessionPreferences(context)
        val activeSession = prefs.activeSessionFlow.first()
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        provideContent {
            GlanceTheme {
                val size = LocalSize.current
                val isTall = size.height >= 100.dp
                
                Row(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .cornerRadius(corner)
                        .clickable(actionStartActivity(intent))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: Session Info
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = GlanceModifier
                                    .size(8.dp)
                                    .background(androidx.glance.unit.ColorProvider(androidx.compose.ui.graphics.Color(0xFFE53935)))
                                    .cornerRadius(4.dp)
                            ) {}
                            Spacer(modifier = GlanceModifier.width(8.dp))
                            Text(
                                text = "Active Session",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = activeSession.selectedSection,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (activeSession.goal != null && isTall) {
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            Text(
                                text = "Goal: ${activeSession.goal}",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                    
                    // Right side: Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.background(GlanceTheme.colors.secondaryContainer).cornerRadius(24.dp).padding(8.dp)
                    ) {
                        Box(
                            modifier = GlanceModifier
                                .size(48.dp)
                                .background(GlanceTheme.colors.surface)
                                .cornerRadius(24.dp)
                                .clickable(actionRunCallback<DecrementCallback>()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-", style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 24.sp, fontWeight = FontWeight.Medium))
                        }
                        
                        Box(
                            modifier = GlanceModifier.width(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = activeSession.currentCount.toString(),
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSecondaryContainer,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        Box(
                            modifier = GlanceModifier
                                .size(48.dp)
                                .background(GlanceTheme.colors.primary)
                                .cornerRadius(24.dp)
                                .clickable(actionRunCallback<IncrementCallback>()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", style = TextStyle(color = GlanceTheme.colors.onPrimary, fontSize = 24.sp, fontWeight = FontWeight.Medium))
                        }
                    }
                }
            }
        }
    }
}

class IncrementCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = ActiveSessionPreferences(context)
        val data = prefs.activeSessionFlow.first()
        prefs.saveActiveSession(data.copy(currentCount = data.currentCount + 1))
        WidgetHelpers.updateAllWidgets(context)
    }
}

class DecrementCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = ActiveSessionPreferences(context)
        val data = prefs.activeSessionFlow.first()
        if (data.currentCount > 0) {
            prefs.saveActiveSession(data.copy(currentCount = data.currentCount - 1))
            WidgetHelpers.updateAllWidgets(context)
        }
    }
}
