package com.example.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.first

class QuickLaunchWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuickLaunchWidget()
}

class QuickLaunchWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetPrefs = WidgetPreferences(context).getConfigFlow(id.hashCode()).first()
        val corner = widgetPrefs.cornerRadius.dp
        
        val homeIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val statsIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "statistics")
        }
        val historyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "history")
        }
        val resumeIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "resume")
        }
        
        provideContent {
            GlanceTheme {
                val size = LocalSize.current
                val isTall = size.height >= 100.dp
                
                if (isTall) {
                    // 2x2 grid
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(GlanceTheme.colors.surface)
                            .cornerRadius(corner)
                            .padding(12.dp)
                    ) {
                        Row(modifier = GlanceModifier.defaultWeight()) {
                            QuickLaunchItem("Home", homeIntent, GlanceModifier.defaultWeight())
                            Spacer(modifier = GlanceModifier.width(8.dp))
                            QuickLaunchItem("Stats", statsIntent, GlanceModifier.defaultWeight())
                        }
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Row(modifier = GlanceModifier.defaultWeight()) {
                            QuickLaunchItem("History", historyIntent, GlanceModifier.defaultWeight())
                            Spacer(modifier = GlanceModifier.width(8.dp))
                            QuickLaunchItem("Resume", resumeIntent, GlanceModifier.defaultWeight())
                        }
                    }
                } else {
                    // 4x1 row
                    Row(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(GlanceTheme.colors.surface)
                            .cornerRadius(corner)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuickLaunchItem("Home", homeIntent, GlanceModifier.defaultWeight())
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        QuickLaunchItem("Stats", statsIntent, GlanceModifier.defaultWeight())
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        QuickLaunchItem("History", historyIntent, GlanceModifier.defaultWeight())
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        QuickLaunchItem("Resume", resumeIntent, GlanceModifier.defaultWeight())
                    }
                }
            }
        }
    }
}

@Composable
fun QuickLaunchItem(label: String, intent: Intent, modifier: GlanceModifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GlanceTheme.colors.secondaryContainer)
            .cornerRadius(16.dp)
            .clickable(actionStartActivity(intent)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = GlanceTheme.colors.onSecondaryContainer,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
