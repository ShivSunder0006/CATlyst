package com.example.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
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
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.TextAlign
import com.example.MainActivity
import com.example.data.AppDatabase
import com.example.repository.SessionRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar

class DailyProgressWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DailyProgressWidget()
}

class DailyProgressWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetPrefs = WidgetPreferences(context).getConfigFlow(id.hashCode()).first()
        val corner = widgetPrefs.cornerRadius.dp
        
        val database = AppDatabase.getDatabase(context)
        val repository = SessionRepository(database.sessionDao())
        
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        
        val todayQuestions = repository.getTotalQuestionsBetween(startOfDay, Long.MAX_VALUE).first() ?: 0
        val dailyGoal = 80 // Could be dynamic
        val percent = if (dailyGoal > 0) (todayQuestions.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        // Pre-generate bitmaps for different sizes to keep UI smooth
        val bitmapLarge = WidgetHelpers.createProgressBitmap(context, percent, 300, 24f)
        val bitmapCompact = WidgetHelpers.createProgressBitmap(context, percent, 400, 32f)

        provideContent {
            GlanceTheme {
                val size = LocalSize.current
                val isWide = size.width >= 200.dp
                
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .cornerRadius(corner)
                        .clickable(actionStartActivity(intent)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWide) {
                        Row(
                            modifier = GlanceModifier.fillMaxSize().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    provider = ImageProvider(bitmapLarge),
                                    contentDescription = "Progress Ring",
                                    modifier = GlanceModifier.size(100.dp)
                                )
                                Text(
                                    text = "${(percent * 100).toInt()}%",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSurface,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = GlanceModifier.width(24.dp))
                            Column(modifier = GlanceModifier.defaultWeight()) {
                                Text(
                                    text = todayQuestions.toString(),
                                    style = TextStyle(
                                        color = GlanceTheme.colors.primary,
                                        fontSize = 56.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "of $dailyGoal questions",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSurfaceVariant,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    } else {
                        // Compact 2x2 layout
                        Box(contentAlignment = Alignment.Center, modifier = GlanceModifier.fillMaxSize().padding(12.dp)) {
                            Image(
                                provider = ImageProvider(bitmapCompact),
                                contentDescription = "Progress Ring",
                                modifier = GlanceModifier.fillMaxSize()
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = todayQuestions.toString(),
                                    style = TextStyle(
                                        color = GlanceTheme.colors.primary,
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "of $dailyGoal",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
